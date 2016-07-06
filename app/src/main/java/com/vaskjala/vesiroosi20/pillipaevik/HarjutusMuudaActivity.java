package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;


import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtneKusimus;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.*;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class HarjutusMuudaActivity extends AppCompatActivity implements LihtsaKusimuseKuulaja {

    private PilliPaevikDatabase mPPManager;
    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutuskord;

    // Vaate lahtrid
    private EditText harjutusekirjelduslahter;
    private TextView alguskuupaevlahter;
    private TextView alguskellaaeglahter;
    private TextView lopukuupaevlahter;
    private TextView lopukellaaeglahter;
    private TextView pikkusminutiteslahter;
    private CheckBox weblinkaruandele;

    private MediaPlayer mPlayer;
    private FileDescriptor mHeliFail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutusmuuda);

        harjutusekirjelduslahter = (EditText) findViewById(R.id.harjutusekirjeldus);
        alguskuupaevlahter = (TextView) findViewById(R.id.alguskuupaev);
        alguskellaaeglahter = (TextView) findViewById(R.id.alguskellaaeg);
        lopukuupaevlahter = (TextView) findViewById(R.id.lopukuupaev);
        lopukellaaeglahter = (TextView) findViewById(R.id.lopukellaaeg);
        pikkusminutiteslahter = (TextView) findViewById(R.id.pikkusminutites);
        weblinkaruandele = (CheckBox) findViewById(R.id.weblinkaruandele);

        Toolbar toolbar = (Toolbar) findViewById(R.id.harjutus_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        assert mAction != null;
        mAction.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            this.teosid = getIntent().getIntExtra("teos_id", 0);
            this.harjutusid = getIntent().getIntExtra("harjutus_id", 0);
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
        } else {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Loen savedInstanceState");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
        }


        mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getApplicationContext());
        this.harjutuskord = harjutuskorradmap.get(this.harjutusid);
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Harjutus : " + this.harjutuskord);
        mAction.setTitle(teos.getNimi());
        AndmedHarjutuskorrastVaatele();
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent output = new Intent();
            if(AndmedHarjutuses()){
                SalvestaHarjutus();
                setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_MUUDA), output);
                if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Harjutus lisatud: " + harjutuskord);
            } else {
                if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Uus harjutus tühi: " + harjutuskord);
                KustutaHarjutus();
            }
            finish();
        }
        if (item.getItemId() == R.id.kustutaharjutus) {
            Bundle args = new Bundle();
            args.putString("kysimus", getString(R.string.dialog_kas_kustuta_harjutuse_kusimus));
            args.putString("jahvastus", getString(R.string.jah));
            args.putString("eivastus", getString(R.string.ei));
            DialogFragment newFragment = new LihtneKusimus();
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "KustutaHarjutus");
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.harjutusmenyy, menu);
        return true;
    }

    @Override
    protected void onStart() {

        if(Tooriistad.kasKasutadaGoogleDrive(getApplicationContext())) {
            if (harjutuskord != null && harjutuskord.getHelifailidriveid() != null &&
                    !harjutuskord.getHelifailidriveid().isEmpty()) {
                AvaFailMangimiseks mAFM = new AvaFailMangimiseks();
                mAFM.execute(harjutuskord.getHelifailidriveid());
            }
        } else if(Tooriistad.KasLubadaSalvestamine(getApplicationContext())){
            if (harjutuskord != null && harjutuskord.getHelifail() != null &&
                    !harjutuskord.getHelifail().isEmpty()) {
                try {
                    FileInputStream in = new FileInputStream(getFilesDir().getPath() + "/" + harjutuskord.getHelifail());
                    mHeliFail = in.getFD();
                    RelativeLayout mangilugu = (RelativeLayout) findViewById(R.id.SalvestuseRiba);
                    mangilugu.setVisibility(RelativeLayout.VISIBLE);
                } catch (IOException e){
                    if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Helifaili avamise viga: " + e.toString());
                }
            }
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Lõpetan mahamängimise");
        if (mPlayer != null) {
            if(mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Lõpetasin mahamängimise");
        }
        super.onStop();
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("teos_id", this.teosid);
        savedInstanceState.putInt("harjutus_id", this.harjutusid);
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onSaveInstanceState: " + this.teosid + " " + this.harjutusid);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        if(AndmedHarjutuses()) {
            SalvestaHarjutus();
            Intent output = new Intent();
            setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_UUS_LISATUD), output);
            if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Harjutuskord : " + this.harjutuskord.toString());
        } else {
            KustutaHarjutus();
        }
        super.onBackPressed();
    }
    private void AndmedHarjutusse() {
        this.harjutuskord.setHarjutusekirjeldus(harjutusekirjelduslahter.getText().toString());
        int uWebLinkAruandele = weblinkaruandele.isChecked() ? 1 : 0;
        this.harjutuskord.setWeblinkaruandele(uWebLinkAruandele);
    }
    private void AndmedHarjutuskorrastVaatele() {
        harjutusekirjelduslahter.setText(harjutuskord.getHarjutusekirjeldus());
        alguskuupaevlahter.setText(Tooriistad.KujundaKuupaev(harjutuskord.getAlgusaeg()));
        alguskellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getAlgusaeg()));
        lopukuupaevlahter.setText(Tooriistad.KujundaKuupaev(harjutuskord.getLopuaeg()));
        lopukellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getLopuaeg()));
        pikkusminutiteslahter.setText(String.valueOf(harjutuskord.ArvutaPikkusminutitesUmardaUles()));
        weblinkaruandele.setChecked(harjutuskord.getWeblinkaruandele()==1);
    }
    private void SalvestaHarjutus (){
        // Kui harjutuse nimi muudetud tühjaks siis anna harjutusele nimi
        String kirjeldus = harjutusekirjelduslahter.getText().toString();
        if (kirjeldus.isEmpty())
            harjutusekirjelduslahter.setText(getString(R.string.vaikimisisharjutusekirjeldus));

        AndmedHarjutusse();
        if(mPPManager == null)
            mPPManager = new PilliPaevikDatabase(getApplicationContext());
        mPPManager.SalvestaHarjutusKord(getApplicationContext(),this.harjutuskord);
    }
    private void KustutaHarjutus(){
        mPPManager.KusututaHarjutus(this.teosid, this.harjutusid);
        Intent output = new Intent();
        setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_KUSTUTATUD), output);
        if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Harjutuskord kustutatud : " + this.harjutusid);
    }
    private boolean AndmedHarjutuses(){
        return harjutuskord.getPikkussekundites() != 0 || !harjutuskord.getAlgusaeg().equals(harjutuskord.getLopuaeg());
    }

    public void KustutaSalvestusKlikk(View v){
        Bundle args = new Bundle();
        args.putString("kysimus", getString(R.string.dialog_kas_kustuta_salvestuse_kusimus));
        args.putString("jahvastus", getString(R.string.jah));
        args.putString("eivastus", getString(R.string.ei));
        DialogFragment newFragment = new LihtneKusimus();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "KustutaSalvestus");
    }

    public void MangiLugu(View v){

        if(v.getId() == R.id.stopp){
            if (mPlayer != null) {
                if(mPlayer.isPlaying())
                    mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                mPlayer = null;
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Lõpetasin mahamängimise");
            }
        } else {
            if (mPlayer != null) {
                mPlayer.stop();
                try {
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) Log.e(getLocalClassName(), "Viga uuesti algusest alustamise" + e.toString());
                }
                if (BuildConfig.DEBUG) Log.d(getLocalClassName(), "Alustasin mängimist algusest");
            } else {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(mHeliFail);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) Log.e(getLocalClassName(), "Viga mahamängimisel" + e.toString());
                }
            }
        }
    }

    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("KustutaHarjutus")) {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "KustutaHarjutus katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        } else
        if(dialog.getTag().equals("KustutaSalvestus")) {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "KustutaSalvestus katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        }
        else {
            if(BuildConfig.DEBUG) Log.e(this.getLocalClassName(), "kuiEiVastus. Tundmatust kohast tuldud !");
        }
    }
    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("KustutaHarjutus")) {
            KustutaHarjutus();
            finish();
        } else
        if(dialog.getTag().equals("KustutaSalvestus")) {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "KustutaSalvestus vastus Jah.");
            RelativeLayout salvestusriba = (RelativeLayout) findViewById(R.id.SalvestuseRiba);
            salvestusriba.setVisibility(RelativeLayout.GONE);
            KustutaSalvestus();
        }
        else {
            if(BuildConfig.DEBUG) Log.e(this.getLocalClassName(), "kuiJahVastus. Tundmatust kohast tuldud !");
        }
    }

    private void KustutaSalvestus(){
        Intent intent = new Intent(this, KustutaFailDraivistTeenus.class);
        intent.putExtra("driveid", harjutuskord.getHelifailidriveid());
        startService(intent);
        Tooriistad.KustutaKohalikFail(getFilesDir(),harjutuskord.getHelifail());
        harjutuskord.TuhjendaSalvestuseValjad();
        SalvestaHarjutus ();
        RelativeLayout mangilugu = (RelativeLayout) findViewById(R.id.SalvestuseRiba);
        mangilugu.setVisibility(RelativeLayout.GONE);
    }

    private class AvaFailMangimiseks extends AsyncTask<String, Void, DriveContents> {

        @Override
        protected DriveContents doInBackground(String... driveIDs) {
            DriveContents dFC = null;
            GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getApplicationContext(), null);
            if(mGDU.LooDriveUhendusAsunkroonselt()) {
                DriveId dID;
                dID = mGDU.AnnaDriveID(driveIDs[0]);
                dFC = mGDU.AvaDriveFail(dID, DriveFile.MODE_READ_ONLY);
            }
            mGDU.KatkestaDriveUhendus();
            return dFC;
        }

        protected void onPostExecute(DriveContents dFC) {

            if(dFC != null){
                mHeliFail = dFC.getParcelFileDescriptor().getFileDescriptor();
                RelativeLayout mangilugu = (RelativeLayout) findViewById(R.id.SalvestuseRiba);
                mangilugu.setVisibility(RelativeLayout.VISIBLE);
            }
            super.onPostExecute(dFC);
        }
    }

}
