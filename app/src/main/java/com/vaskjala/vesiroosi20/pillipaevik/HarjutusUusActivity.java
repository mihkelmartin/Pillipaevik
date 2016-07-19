package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtneKusimus;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.LisaFailDraiviTeenus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Calendar;
import java.util.HashMap;


public class HarjutusUusActivity extends AppCompatActivity implements LihtsaKusimuseKuulaja {

    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutus;
    private boolean taimertootab = false;
    private boolean bkasSalvestame = false;
    private long stardiaeg = 0;
    private long kulunudaeg = 0;
    private long salvestuseaeg;
    private static final short viiv = 300;

    private static TextView timer;
    private static Button kaivitaTimerNupp;
    private static Button mikrofoniLulitiNupp;

    private MediaRecorder mRecorder = null;

    protected void onStart() {
        if(taimertootab)
            handler.postDelayed(runnable, viiv);
        super.onStart();
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("harjutusid", this.harjutusid);
        savedInstanceState.putLong("stardiaeg", this.stardiaeg);
        savedInstanceState.putLong("kulunudaeg", this.kulunudaeg);
        savedInstanceState.putBoolean("taimertootab", this.taimertootab);
        savedInstanceState.putBoolean("kasSalvestame", this.bkasSalvestame);
        if(BuildConfig.DEBUG) Log.d("HarjutusUusActivity", "onSaveInstanceState " + this.harjutusid + " " + this.stardiaeg + " " +
                this.kulunudaeg + " Taimer sees:" +this.taimertootab + " Kas salvestame: " + bkasSalvestame);

        super.onSaveInstanceState(savedInstanceState);
    }
    protected void onStop() {
        if(BuildConfig.DEBUG) Log.d("HarjutusUusActivity", "On Stop");
        SeisataLindistaja();
        if(taimertootab)
            handler.removeCallbacks(runnable);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutus_uus);

        Toolbar toolbar = (Toolbar) findViewById(R.id.uusharjutus_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        assert mAction != null;
        mAction.setDisplayHomeAsUpEnabled(true);

        this.teosid = getIntent().getIntExtra("teos_id", 0);
        this.harjutusid = getIntent().getIntExtra("harjutusid", 0);
        if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Teos : " + this.teosid + " Harjutus : " + this.harjutusid);

        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        mAction.setTitle(teos.getNimi());

        timer = (TextView) findViewById(R.id.timer);
        kaivitaTimerNupp = (Button) findViewById(R.id.kaivitataimernupp);
        mikrofoniLulitiNupp = (Button) findViewById(R.id.mikrofoniluliti);

        if (savedInstanceState == null) {
            this.harjutus = new HarjutusKord(this.teosid);
            harjutus.Salvesta(getApplicationContext());
            this.harjutusid = this.harjutus.getId();
            if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Uus harjutus loodud : " + this.harjutusid);
        }else {
            this.harjutusid = savedInstanceState.getInt("harjutusid");
            this.stardiaeg = savedInstanceState.getLong("stardiaeg");
            this.kulunudaeg = savedInstanceState.getLong("kulunudaeg");
            this.taimertootab = savedInstanceState.getBoolean("taimertootab");
            this.bkasSalvestame = savedInstanceState.getBoolean("kasSalvestame");
            if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Loen savedinstantsist :" + this.harjutusid + " " +
                    this.stardiaeg + " " + this.kulunudaeg + " Taimer sees:" +this.taimertootab);

            HashMap<Integer, HarjutusKord> harjutuskorradmap  = teos.getHarjutuskorradmap(getApplicationContext());
            this.harjutus = harjutuskorradmap.get(this.harjutusid);
            if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Harjutus taastatud teose kaudu : " + this.harjutusid);

            if(taimertootab)
                kaivitaTimerNupp.setText(getResources().getText(R.string.katkesta));
            else
                kaivitaTimerNupp.setText(getResources().getText(R.string.jatka));

            // Taimer on pausil, kuid on juba lugenud aega
            if(!taimertootab && kulunudaeg != 0) {
                timer.setText(String.valueOf(Tooriistad.KujundaAeg(kulunudaeg)));
            }
        }
        SeadistaMikrofoniNupp();

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if(taimertootab) {
            SeisataLindistaja();
            SeisataTaimer();
        }
        if(item.getItemId() == android.R.id.home){
            if(kuiAndmedHarjutuses()) {
                SalvestaHarjutus();
                Intent output = new Intent();
                setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_UUS_LISATUD), output);
                if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Harjutuskord : " + this.harjutus.toString());
            } else {
                KustutaHarjutus();
            }
            finish();
        }
        if(item.getItemId() == R.id.kustutaharjutus){
            Bundle args = new Bundle();
            args.putString("kysimus",getString(R.string.dialog_kas_kustuta_harjutuse_kusimus));
            args.putString("jahvastus",getString(R.string.jah));
            args.putString("eivastus",getString(R.string.ei));
            DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "Kustuta Harjutus");
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.harjutusmenyy, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(taimertootab) {
            SeisataLindistaja();
            SeisataTaimer();
        }
        if(kuiAndmedHarjutuses()) {
            SalvestaHarjutus();
            Intent output = new Intent();
            setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_UUS_LISATUD), output);
            if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Harjutuskord : " + this.harjutus.toString());
        } else {
            KustutaHarjutus();
        }
        super.onBackPressed();
    }

    private void AndmedHarjutusse(HarjutusKord harjutus){
        String kirjeldus = ((EditText)findViewById(R.id.harjutusekirjeldus)).getText().toString();
        harjutus.setHarjutusekirjeldus(kirjeldus);
    }
    private void SalvestaHarjutus(){
        EditText ETharjutusekirjeldus = (EditText) findViewById(R.id.harjutusekirjeldus);
        String kirjeldus = ETharjutusekirjeldus.getText().toString();
        if (kirjeldus.isEmpty())
            ETharjutusekirjeldus.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));

        AndmedHarjutusse(this.harjutus);
        harjutus.Salvesta(getApplicationContext());
    }
    private void KustutaHarjutus(){
        harjutus.Kustuta(getApplicationContext());
        if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Uus harjutuskord kustutatud : " + this.harjutusid);
        Intent output = new Intent();
        setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_UUS_LOOMATA), output);
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long aeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;
            if(bkasSalvestame && (System.currentTimeMillis() - salvestuseaeg) > Tooriistad.MAKSIMAALNE_HELIFAILIPIKKUS_MILLISEKUNDITES){
                bkasSalvestame = !bkasSalvestame;
                SeisataLindistaja();
                SeadistaMikrofoniNupp();
            }
            timer.setText(String.valueOf( Tooriistad.KujundaAeg(aeg)));
            handler.postDelayed(this, viiv);
        }
    };

    public void LulitaMikrofon(View v) {
        bkasSalvestame = !bkasSalvestame;
        SeadistaMikrofoniNupp();
    }

    public void KaivitaTaimer(View v){

        if(taimertootab) {
            mikrofoniLulitiNupp.setEnabled(true);
            SeisataLindistaja();
            SeisataTaimer();
            harjutus.Salvesta(getApplicationContext());
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
            harjutus.Salvesta(getApplicationContext());
        }
        taimertootab = true;
        this.stardiaeg = System.currentTimeMillis();
        handler.postDelayed(runnable, viiv);
    }
    private void SeisataTaimer(){
        taimertootab = false;
        kulunudaeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;
        harjutus.setLopuaegEiArvuta(Calendar.getInstance().getTime());
        harjutus.setPikkussekundites((int) (kulunudaeg / 1000));
        handler.removeCallbacks(runnable);
    }

    private void KaivitaLindistaja(){
        if(bkasSalvestame) {
            if(harjutus.getHelifail() == null || harjutus.getHelifail().isEmpty())
                harjutus.setHelifail(harjutus.MoodustaFailiNimi());
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Fail:" + harjutus.getHelifail());
            mRecorder = new MediaRecorder();
            try {
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setOutputFile(getFilesDir().getPath() + "/" + harjutus.getHelifail());
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.prepare();
                mRecorder.start();
                salvestuseaeg = System.currentTimeMillis();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } catch (Exception e) {
                mRecorder = null;
                if(BuildConfig.DEBUG) Log.e(getLocalClassName(), "Lindistamist ei suudetud alustada:" + e.toString());
            }
        }
    }
    private void SeisataLindistaja() {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Lõpetan lindistamise");
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            if(Tooriistad.kasKasutadaGoogleDrive(getApplicationContext())) {
                Intent intent = new Intent(this, LisaFailDraiviTeenus.class);
                intent.putExtra("teosid", harjutus.getTeoseid());
                intent.putExtra("harjutusid", harjutus.getId());
                startService(intent);
                if (BuildConfig.DEBUG) Log.d(getLocalClassName(), "Lõpetasin lindistamise");
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

        }
    }

    private void SeadistaMikrofoniNupp(){
        if(Tooriistad.KasLubadaSalvestamine(getApplicationContext())) {
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
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Kustutamine katkestatud:" + this.teosid);
    }
    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        KustutaHarjutus();
        finish();
    }

    private boolean kuiAndmedHarjutuses(){
        return harjutus.getPikkussekundites() != 0 || !harjutus.getAlgusaeg().equals(harjutus.getLopuaeg());
    }

}
