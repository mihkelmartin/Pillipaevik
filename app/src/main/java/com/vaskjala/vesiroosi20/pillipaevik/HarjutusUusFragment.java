package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtneKusimus;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.LisaFailDraiviTeenus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Calendar;
import java.util.HashMap;


public class HarjutusUusFragment extends Fragment implements LihtsaKusimuseKuulaja, View.OnClickListener {

    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutus;
    private boolean taimertootab = false;
    private boolean bkasSalvestame = false;
    private long stardiaeg = 0;
    private long kulunudaeg = 0;
    private static final short viiv = 300;
    private final Handler handler = new Handler();

    private HarjutusUusFragmendiKuulaja harjutusUusFragmendiKuulaja;

    private static TextView timer;
    private static Button kaivitaTimerNupp;
    private static Button mikrofoniLulitiNupp;

    private MediaRecorder mRecorder = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            harjutusUusFragmendiKuulaja = (HarjutusUusFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama HarjutusUusFragmendiKuulaja");
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            harjutusUusFragmendiKuulaja = (HarjutusUusFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama HarjutusUusFragmendiKuulaja");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "savedInstanceState == null");
            Bundle algargumendid = getArguments();
            if(algargumendid != null) {
                this.teosid = algargumendid.getInt("teos_id", 0);
                this.harjutusid = algargumendid.getInt("harjutusid", 0);
            } else {
                if (getActivity() != null && getActivity().getIntent() != null) {
                    this.teosid = getActivity().getIntent().getIntExtra("teos_id", 0);
                    this.harjutusid = getActivity().getIntent().getIntExtra("harjutusid", 0);
                }
            }
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
        }else {
            this.teosid= savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutusid");
            this.stardiaeg = savedInstanceState.getLong("stardiaeg");
            this.kulunudaeg = savedInstanceState.getLong("kulunudaeg");
            this.taimertootab = savedInstanceState.getBoolean("taimertootab");
            this.bkasSalvestame = savedInstanceState.getBoolean("kasSalvestame");
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Loen savedinstantsist :" + this.harjutusid + " " +
                    this.stardiaeg + " " + this.kulunudaeg + " Taimer sees:" +this.taimertootab);

            PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getActivity().getApplicationContext());
            Teos teos = mPPManager.getTeos(this.teosid);
            HashMap<Integer, HarjutusKord> harjutuskorradmap  = teos.getHarjutuskorradmap(getActivity().getApplicationContext());
            this.harjutus = harjutuskorradmap.get(this.harjutusid);
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Harjutus taastatud teose kaudu : " + this.harjutusid);

            if(taimertootab)
                kaivitaTimerNupp.setText(getResources().getText(R.string.katkesta));
            else
                kaivitaTimerNupp.setText(getResources().getText(R.string.jatka));

            // Taimer on pausil, kuid on juba lugenud aega
            if(!taimertootab && kulunudaeg != 0) {
                timer.setText(String.valueOf(Tooriistad.KujundaAeg(kulunudaeg)));
            }
        }
        if(harjutus == null){
            this.harjutus = new HarjutusKord(this.teosid);
            harjutus.Salvesta(getActivity().getApplicationContext());
            this.harjutusid = this.harjutus.getId();
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Uus harjutus loodud : " + this.harjutusid);
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
    @Override
    public void onPause() {

        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment","onPause");
        super.onPause();
        if(this.harjutus != null) {
            SalvestaHarjutus();
        }
    }
    public void onStop() {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "On Stop");
        SeisataLindistaja();
        if(taimertootab)
            handler.removeCallbacks(AjaUuendaja);
        super.onStop();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("teos_id", this.teosid);
        savedInstanceState.putInt("harjutusid", this.harjutusid);
        savedInstanceState.putLong("stardiaeg", this.stardiaeg);
        savedInstanceState.putLong("kulunudaeg", this.kulunudaeg);
        savedInstanceState.putBoolean("taimertootab", this.taimertootab);
        savedInstanceState.putBoolean("kasSalvestame", this.bkasSalvestame);
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "onSaveInstanceState " + this.teosid + " " + this.harjutusid +
                " " + this.stardiaeg + " " + this.kulunudaeg + " Taimer sees:" +this.taimertootab +
                " Kas salvestame: " + bkasSalvestame);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.harjutusmenyy, menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.kustutaharjutus){
            Bundle args = new Bundle();
            args.putString("kysimus",getString(R.string.dialog_kas_kustuta_harjutuse_kusimus));
            args.putString("jahvastus",getString(R.string.jah));
            args.putString("eivastus",getString(R.string.ei));
            DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.show(getChildFragmentManager(), "Kustuta Harjutus");
        }
        return super.onOptionsItemSelected(item);
    }

    private void AndmedHarjutusse(HarjutusKord harjutus){
        String kirjeldus = ((EditText)getView().findViewById(R.id.harjutusekirjeldus)).getText().toString();
        harjutus.setHarjutusekirjeldus(kirjeldus);
    }

    public void SuleHarjutus(){
        if(taimertootab) {
            SeisataLindistaja();
            SeisataTaimer();
        }
        if(AndmedHarjutuses()) {
            // Kui harjutuse nimi muudetud tühjaks siis anna harjutusele nimi
            EditText Harjutusekirjeldus = (EditText) getView().findViewById(R.id.harjutusekirjeldus);
            String kirjeldus = Harjutusekirjeldus.getText().toString();
            if (kirjeldus.isEmpty())
                Harjutusekirjeldus.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));
            SalvestaHarjutus();
        } else {
            KustutaHarjutus();
        }
    }
    private void SalvestaHarjutus(){
        AndmedHarjutusse(this.harjutus);
        harjutus.Salvesta(getActivity().getApplicationContext());
    }
    private void KustutaHarjutus(){
        harjutus.Kustuta(getActivity().getApplicationContext());
        harjutus = null;
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
            harjutus.Salvesta(getActivity().getApplicationContext());
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
            harjutus.setAlgusaeg(Tooriistad.HetkeKuupaevNullitudSekunditega());
            harjutus.Salvesta(getActivity().getApplicationContext());
        }
        taimertootab = true;
        this.stardiaeg = System.currentTimeMillis();
        handler.postDelayed(AjaUuendaja, viiv);
    }
    private void SeisataTaimer(){
        taimertootab = false;
        kulunudaeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;
        harjutus.setLopuaegEiArvuta(Calendar.getInstance().getTime());
        harjutus.setPikkussekundites((int) (kulunudaeg / 1000));
        handler.removeCallbacks(AjaUuendaja);
    }

    private void KaivitaLindistaja(){
        if(bkasSalvestame) {
            if(harjutus.getHelifail() == null || harjutus.getHelifail().isEmpty())
                harjutus.setHelifail(harjutus.MoodustaFailiNimi());
            if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Fail:" + harjutus.getHelifail());
            mRecorder = new MediaRecorder();
            try {
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setOutputFile(getActivity().getFilesDir().getPath() + "/" + harjutus.getHelifail());
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
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            if(Tooriistad.kasKasutadaGoogleDrive(getActivity().getApplicationContext())) {
                Intent intent = new Intent(getActivity(), LisaFailDraiviTeenus.class);
                intent.putExtra("teosid", harjutus.getTeoseid());
                intent.putExtra("harjutusid", harjutus.getId());
                getActivity().startService(intent);
                if (BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Lõpetasin lindistamise");
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

        }
    }
    private void SeadistaMikrofoniNupp(){
        if(Tooriistad.KasLubadaSalvestamine(getActivity().getApplicationContext())) {
            if (bkasSalvestame) {
                mikrofoniLulitiNupp.setText(getResources().getText(R.string.sees));
                mikrofoniLulitiNupp.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mic_black_18dp, null), null, null, null);
            } else {
                mikrofoniLulitiNupp.setText(getResources().getText(R.string.valjas));
                mikrofoniLulitiNupp.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mic_off_black_18dp, null), null, null, null);
            }
        } else {
            mikrofoniLulitiNupp.setVisibility(Button.GONE);
        }
    }
    
    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusFragment", "Kustutamine katkestatud:" + this.teosid);
    }
    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        KustutaHarjutus();
        harjutusUusFragmendiKuulaja.KustutaHarjutus(this.harjutusid);
    }

    private boolean AndmedHarjutuses(){
        return harjutus.getPikkussekundites() != 0 || !harjutus.getAlgusaeg().equals(harjutus.getLopuaeg());
    }

}
