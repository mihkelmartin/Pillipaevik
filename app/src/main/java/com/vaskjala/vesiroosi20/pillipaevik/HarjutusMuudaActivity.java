package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.*;
import java.util.HashMap;

public class HarjutusMuudaActivity extends AppCompatActivity implements LihtsaKusimuseKuulaja {

    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutuskord;
    private int itemposition;

    // Vaate lahtrid
    private EditText harjutusekirjelduslahter;
    private TextView alguskuupaevlahter;
    private TextView alguskellaaeglahter;
    private TextView pikkusminutiteslahter;
    private CheckBox weblinkaruandele;

    private MediaPlayer mPlayer = null;
    private FileDescriptor mHeliFail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutusmuuda);

        harjutusekirjelduslahter = (EditText) findViewById(R.id.harjutusekirjeldus);
        alguskuupaevlahter = (TextView) findViewById(R.id.alguskuupaev);
        alguskellaaeglahter = (TextView) findViewById(R.id.alguskellaaeg);
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
            this.itemposition = getIntent().getIntExtra("item_position", 0);
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
        } else {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Loen savedInstanceState");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
            this.itemposition = savedInstanceState.getInt("item_position");
        }


        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
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
            output.putExtra("item_position", itemposition);
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
                mAFM.execute(harjutuskord);
            }
        } else if(Tooriistad.KasLubadaSalvestamine(getApplicationContext())){
            if (harjutuskord != null && harjutuskord.getHelifail() != null &&
                    !harjutuskord.getHelifail().isEmpty()) {
                try {
                    FileInputStream in = new FileInputStream(getFilesDir().getPath() + "/" + harjutuskord.getHelifail());
                    mHeliFail = in.getFD();
                    SeadistaSalvestiseRiba();
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
        savedInstanceState.putInt("item_position", this.itemposition);
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onSaveInstanceState: " + this.teosid + " " + this.harjutusid);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        if(AndmedHarjutuses()) {
            SalvestaHarjutus();
            Intent output = new Intent();
            output.putExtra("item_position", itemposition);
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
        alguskuupaevlahter.setText(Tooriistad.KujundaKuupaevSonalineLuhike(harjutuskord.getAlgusaeg()));
        alguskellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getAlgusaeg()));
        pikkusminutiteslahter.setText(Tooriistad.KujundaHarjutusteMinutidTabloo(harjutuskord.ArvutaPikkusminutitesUmardaUles()));
        weblinkaruandele.setChecked(harjutuskord.getWeblinkaruandele()==1);
    }
    private void SalvestaHarjutus (){
        // Kui harjutuse nimi muudetud tühjaks siis anna harjutusele nimi
        String kirjeldus = harjutusekirjelduslahter.getText().toString();
        if (kirjeldus.isEmpty())
            harjutusekirjelduslahter.setText(getString(R.string.vaikimisisharjutusekirjeldus));

        AndmedHarjutusse();
        harjutuskord.Salvesta(getApplicationContext());
    }
    private void KustutaHarjutus(){
        harjutuskord.Kustuta(getApplicationContext());
        Intent output = new Intent();
        output.putExtra("item_position", itemposition);
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
                    mPlayer = null;
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
                    mPlayer = null;
                }
            }
        }
    }

    public void JagaLugu(View v){
        if(Tooriistad.kasNimedEpostOlemas(getApplicationContext())) {
            Bundle args = new Bundle();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, MoodustaJagamiseTeema());
            i.putExtra(Intent.EXTRA_TEXT, MoodustaJagamiseTekst());
            try {
                startActivity(Intent.createChooser(i, getString(R.string.aruanne_saada)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getParent(), getString(R.string.aruanne_eposti_rakendus_puudub), Toast.LENGTH_SHORT).show();
            }
        } else {
            Tooriistad.NaitaHoiatust((Activity) v.getContext(),
                    getString(R.string.jagamise_keeldumise_pealkiri),
                    getString(R.string.jagamise_keeldumise_pohjus));
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
        mHeliFail = null;
        harjutuskord.TuhjendaSalvestuseValjad();
        SalvestaHarjutus ();
        SeadistaSalvestiseRiba();

    }

    private class AvaFailMangimiseks extends AsyncTask<HarjutusKord, Void, DriveContents> {

        @Override
        protected DriveContents doInBackground(HarjutusKord... harjutusKords) {
            DriveContents dFC = null;
            GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getApplicationContext(), null);
            if(mGDU.LooDriveUhendusAsunkroonselt()) {
                DriveId dID;
                dID = mGDU.AnnaDriveID(harjutusKords[0].getHelifailidriveid());
                try {
                    dFC = mGDU.AvaDriveFail(dID, DriveFile.MODE_READ_ONLY);
                } catch (FileNotFoundException e){
                    if(BuildConfig.DEBUG) Log.e("AvaFailMangimiseks", "Faili ei leitud. Tühjendame väljad. " + e.toString());
                    harjutusKords[0].TuhjendaSalvestuseValjad();
                    harjutusKords[0].Salvesta(getApplicationContext());
                }
            }
            mGDU.KatkestaDriveUhendus();
            return dFC;
        }

        protected void onPostExecute(DriveContents dFC) {

            if(dFC != null){
                mHeliFail = dFC.getParcelFileDescriptor().getFileDescriptor();
            }
            SeadistaSalvestiseRiba();
            super.onPostExecute(dFC);
        }
    }

    public void SeadistaSalvestiseRiba() {
        RelativeLayout mangilugu = (RelativeLayout) findViewById(R.id.SalvestuseRiba);
        if (mHeliFail == null) {
            mangilugu.setVisibility(RelativeLayout.GONE);
        } else {
            mangilugu.setVisibility(RelativeLayout.VISIBLE);
            CheckBox mLinkAruandele = (CheckBox)findViewById(R.id.weblinkaruandele);
            ImageButton mJaga = (ImageButton)findViewById(R.id.jaga);
            if(harjutuskord.getHelifailidriveweblink() == null ||
                    harjutuskord.getHelifailidriveweblink().isEmpty()){
                mLinkAruandele.setVisibility(CheckBox.GONE);
                mJaga.setVisibility(ImageButton.GONE);
            }
        }
    }

    private String MoodustaJagamiseTeema(){
        String retVal;
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        retVal = teos.getNimi() + " " + getString(R.string.jagamise_teema_harjutus) + ", " +
                Tooriistad.KujundaKuupaevSonaline(harjutuskord.getAlgusaeg());
        return retVal;
    }
    private String MoodustaJagamiseTekst(){
        String retVal;
        String ReaVahetus = System.getProperty("line.separator");
        retVal = getString(R.string.tere) + "!" + ReaVahetus + ReaVahetus;
        retVal = retVal + getString(R.string.jagamise_sisu_link_harjutusele) + ": " +
                harjutuskord.getHelifailidriveweblink() + ReaVahetus + ReaVahetus;

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        retVal = retVal + getString(R.string.aruanne_tervitades) + ReaVahetus +
                sharedPref.getString("minueesnimi","") + " " + sharedPref.getString("minuperenimi","") + " " +
                getString(R.string.aruanne_pillipaeviku_vahendusel);
        return retVal;
    }
}
