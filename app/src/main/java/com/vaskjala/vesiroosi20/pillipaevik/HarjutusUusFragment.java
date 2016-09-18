package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.DialogFragment;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.LisaFailDraiviTeenus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Calendar;
import java.util.HashMap;


public class HarjutusUusFragment extends HarjutusFragment {

    private boolean taimertootab = false;
    private boolean bkasSalvestame = false;
    private long stardiaeg = 0;
    private long kulunudaeg = 0;
    private static final short viiv = 300;
    private final Handler handler = new Handler();

    private static TextView timer;
    private static Button kaivitaTimerNupp;
    private static Button mikrofoniLulitiNupp;

    private MediaRecorder mRecorder = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "savedInstanceState == null");
            Bundle algargumendid = getArguments();
            if(algargumendid != null) {
                setTeosid(algargumendid.getInt("teos_id", 0));
                setHarjutusid(algargumendid.getInt("harjutus_id", 0));
            } else {
                if (getActivity() != null && getActivity().getIntent() != null) {
                    setTeosid(getActivity().getIntent().getIntExtra("teos_id", 0));
                    setHarjutusid(getActivity().getIntent().getIntExtra("harjutus_id", 0));
                }
            }
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Teos : " + getTeosid() + " Harjutus:" + getHarjutusid());
        }else {
            setTeosid(savedInstanceState.getInt("teos_id"));
            setHarjutusid(savedInstanceState.getInt("harjutus_id"));
            this.stardiaeg = savedInstanceState.getLong("stardiaeg");
            this.kulunudaeg = savedInstanceState.getLong("kulunudaeg");
            this.taimertootab = savedInstanceState.getBoolean("taimertootab");
            this.bkasSalvestame = savedInstanceState.getBoolean("kasSalvestame");
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Loen savedinstantsist :" + getHarjutusid() + " " +
                    this.stardiaeg + " " + this.kulunudaeg + " Taimer sees:" +this.taimertootab);

            PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());
            Teos teos = mPPManager.getTeos(getTeosid());
            HashMap<Integer, HarjutusKord> harjutuskorradmap  = teos.getHarjutuskorradmap(getActivity().getApplicationContext());
            setHarjutuskord(harjutuskorradmap.get(getHarjutusid()));
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Harjutus taastatud teose kaudu : " + getHarjutusid());

            if(taimertootab)
                kaivitaTimerNupp.setText(getResources().getText(R.string.katkesta));
            else
                kaivitaTimerNupp.setText(getResources().getText(R.string.jatka));

            // Taimer on pausil, kuid on juba lugenud aega
            if(!taimertootab && kulunudaeg != 0) {
                timer.setText(String.valueOf(Tooriistad.KujundaAeg(kulunudaeg)));
            }
        }
        if(getHarjutuskord() == null){
            setHarjutuskord(new HarjutusKord(getTeosid()));
            getHarjutuskord().Salvesta(getActivity().getApplicationContext());
            setHarjutusid(getHarjutuskord().getId());
            getHarjutusFragmendiKuulaja().SeaHarjutusid(getHarjutusid());
            getHarjutusFragmendiKuulaja().VarskendaHarjutusteList();
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Uus harjutus loodud : " + getHarjutusid());
        }
    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_harjutusuus, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        timer = (TextView) getView().findViewById(R.id.timer);
        kaivitaTimerNupp = (Button) getView().findViewById(R.id.kaivitataimernupp);
        mikrofoniLulitiNupp = (Button) getView().findViewById(R.id.mikrofoniluliti);
        kaivitaTimerNupp.setOnClickListener(this);
        mikrofoniLulitiNupp.setOnClickListener(this);
        SeadistaMikrofoniNupp();
    }
    public void onStart() {
        if(taimertootab)
            handler.postDelayed(AjaUuendaja, viiv);
        super.onStart();
    }

    public void onStop() {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "On Stop");
        SeisataLindistaja();
        if(taimertootab)
            handler.removeCallbacks(AjaUuendaja);
        super.onStop();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putLong("stardiaeg", this.stardiaeg);
        savedInstanceState.putLong("kulunudaeg", this.kulunudaeg);
        savedInstanceState.putBoolean("taimertootab", this.taimertootab);
        savedInstanceState.putBoolean("kasSalvestame", this.bkasSalvestame);
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "onSaveInstanceState " +
                " " + this.stardiaeg + " " + this.kulunudaeg + " Taimer sees:" +this.taimertootab +
                " Kas salvestame: " + bkasSalvestame);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void AndmedHarjutusse(){
        String kirjeldus = ((EditText)getView().findViewById(R.id.harjutusekirjeldus)).getText().toString();
        getHarjutuskord().setHarjutusekirjeldus(kirjeldus);
    }

    public void SuleHarjutus(){
        if(taimertootab) {
            SeisataLindistaja();
            SeisataTaimer();
        }
        super.SuleHarjutus();
    }

    private final Runnable salvestusajalimiit = new Runnable() {
        @Override
        public void run() {
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Ajalimiit täis. Lõpetame salvestamise");
            if(bkasSalvestame) {
                bkasSalvestame = !bkasSalvestame;
                SeisataLindistaja();
                SeadistaMikrofoniNupp();
            }
        }
    };
    private final Runnable AjaUuendaja = new Runnable() {
        @Override
        public void run() {
            long aeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;
            timer.setText(String.valueOf( Tooriistad.KujundaAeg(aeg)));
            handler.postDelayed(this, viiv);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kaivitataimernupp:
                KaivitaTaimer(v);
                break;
            case R.id.mikrofoniluliti:
                LulitaMikrofon(v);
                break;
            default:
                break;
        }
    }

    public void LulitaMikrofon(View v) {
        bkasSalvestame = !bkasSalvestame;
        SeadistaMikrofoniNupp();
    }
    public void KaivitaTaimer(View v){

        if(taimertootab) {
            mikrofoniLulitiNupp.setEnabled(true);
            SeisataLindistaja();
            SeisataTaimer();
            getHarjutuskord().Salvesta(getActivity().getApplicationContext());
            kaivitaTimerNupp.setText(getResources().getText(R.string.jatka));
        } else {
            mikrofoniLulitiNupp.setEnabled(false);
            KaivitaLindistaja();
            KaivitaTaimer();
            kaivitaTimerNupp.setText(getResources().getText(R.string.katkesta));
        }
    }

    private void KaivitaTaimer(){
        if(stardiaeg == 0) {
            getHarjutuskord().setAlgusaeg(Tooriistad.HetkeKuupaevNullitudSekunditega());
            getHarjutuskord().Salvesta(getActivity().getApplicationContext());
        }
        taimertootab = true;
        this.stardiaeg = System.currentTimeMillis();
        handler.postDelayed(AjaUuendaja, viiv);
    }
    private void SeisataTaimer(){
        taimertootab = false;
        kulunudaeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;
        getHarjutuskord().setLopuaegEiArvuta(Calendar.getInstance().getTime());
        getHarjutuskord().setPikkussekundites((int) (kulunudaeg / 1000));
        handler.removeCallbacks(AjaUuendaja);
    }

    private void KaivitaLindistaja(){
        if(bkasSalvestame) {
            HarjutusKord harjutusKord = getHarjutuskord();
            if(harjutusKord.getHelifail() == null || harjutusKord.getHelifail().isEmpty())
                harjutusKord.setHelifail(harjutusKord.MoodustaFailiNimi());
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Fail:" + harjutusKord.getHelifail());
            mRecorder = new MediaRecorder();
            try {
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setOutputFile(getActivity().getFilesDir().getPath() + "/" + harjutusKord.getHelifail());
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.prepare();
                mRecorder.start();
                handler.postDelayed(salvestusajalimiit, Tooriistad.MAKSIMAALNE_HELIFAILIPIKKUS_MILLISEKUNDITES);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } catch (Exception e) {
                mRecorder = null;
                if(BuildConfig.DEBUG) Log.e("HarjutusUusFragment", "Lindistamist ei suudetud alustada:" + e.toString());
            }
        }
    }
    private void SeisataLindistaja() {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Lõpetan lindistamise");
        handler.removeCallbacks(salvestusajalimiit);
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;

                if(Tooriistad.kasKasutadaGoogleDrive(getActivity().getApplicationContext())) {
                    Intent intent = new Intent(getActivity(), LisaFailDraiviTeenus.class);
                    intent.putExtra("teosid", getHarjutuskord().getTeoseid());
                    intent.putExtra("harjutusid", getHarjutuskord().getId());
                    getActivity().startService(intent);
                    if (BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Lõpetasin lindistamise");
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            } catch (Exception e) {
                mRecorder.release();
                mRecorder = null;
                getHarjutuskord().KustutaFailid(getActivity().getApplicationContext());
                getHarjutuskord().TuhjendaSalvestuseValjad();
                if(BuildConfig.DEBUG) Log.e("HarjutusUusFragment", "Lindistamist ei suudetud lõpetada:" + e.toString());
            }
        }
    }
    private void SeadistaMikrofoniNupp(){
        if(Tooriistad.KasLubadaSalvestamine(getActivity().getApplicationContext())) {
            if (bkasSalvestame) {
                mikrofoniLulitiNupp.setText(getResources().getText(R.string.sees));
                mikrofoniLulitiNupp.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mic_black, null), null, null, null);
            } else {
                mikrofoniLulitiNupp.setText(getResources().getText(R.string.valjas));
                mikrofoniLulitiNupp.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mic_off_black, null), null, null, null);
            }
        } else {
            mikrofoniLulitiNupp.setVisibility(Button.GONE);
        }
    }
    
    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Kustutamine katkestatud:" + getTeosid());
    }
    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        KustutaHarjutus();
        getHarjutusFragmendiKuulaja().KustutaHarjutus(getHarjutusid());
    }
}
