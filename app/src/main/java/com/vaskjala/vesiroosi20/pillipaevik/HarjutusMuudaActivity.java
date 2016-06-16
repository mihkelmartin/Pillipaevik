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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtneKusimus;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.GoogleDriveUhendus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.io.IOException;
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

    private MediaPlayer mPlayer;
    private DriveContents mHeliFailDrive = null;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.harjutus_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        assert mAction != null;
        mAction.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            this.teosid = getIntent().getIntExtra("teos_id", 0);
            this.harjutusid = getIntent().getIntExtra("harjutus_id", 0);
            Log.d(getLocalClassName(), "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
        } else {
            Log.d(getLocalClassName(), "Loen savedInstanceState");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
        }


        mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getApplicationContext());
        this.harjutuskord = harjutuskorradmap.get(this.harjutusid);
        Log.d(getLocalClassName(), "Harjutus : " + this.harjutuskord);
        mAction.setTitle(teos.getNimi());
        AndmedHarjutuskorrastVaatele();
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent output = new Intent();
            if(AndmedHarjutuses()){
                SalvestaHarjutus();
                setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_MUUDA), output);
                Log.d(this.getLocalClassName(), "Harjutus lisatud: " + harjutuskord);
            } else {
                Log.d(this.getLocalClassName(), "Uus harjutus tühi: " + harjutuskord);
                KustutaHarjutus();
            }
            finish();
        }
        if (item.getItemId() == R.id.kustutaharjutus) {
            Bundle args = new Bundle();
            args.putString("kysimus", "Kustutad Harjutuse ?");
            args.putString("jahvastus", "Jah");
            args.putString("eivastus", "Ei");
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

        if(harjutuskord!= null && harjutuskord.getHelifailidriveid() != null &&
                !harjutuskord.getHelifailidriveid().isEmpty()) {
            AvaFailMangimiseks mAFM = new AvaFailMangimiseks();
            mAFM.execute(harjutuskord.getHelifailidriveid());
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(getLocalClassName(), "Lõpetan mahamängimise");
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            Log.d(getLocalClassName(), "Lõpetasin mahamängimise");
        }
        super.onStop();
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("teos_id", this.teosid);
        savedInstanceState.putInt("harjutus_id", this.harjutusid);
        Log.d(getLocalClassName(), "onSaveInstanceState: " + this.teosid + " " + this.harjutusid);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        if(AndmedHarjutuses()) {
            SalvestaHarjutus();
            Intent output = new Intent();
            setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_UUS_LISATUD), output);
            Log.d(this.getLocalClassName(), "Harjutuskord : " + this.harjutuskord.toString());
        } else {
            KustutaHarjutus();
        }
        super.onBackPressed();
    }
    private void AndmedHarjutusse() {
        this.harjutuskord.setHarjutusekirjeldus(harjutusekirjelduslahter.getText().toString());
    }
    private void AndmedHarjutuskorrastVaatele() {
        harjutusekirjelduslahter.setText(harjutuskord.getHarjutusekirjeldus());
        alguskuupaevlahter.setText(Tooriistad.KujundaKuupaev(harjutuskord.getAlgusaeg()));
        alguskellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getAlgusaeg()));
        lopukuupaevlahter.setText(Tooriistad.KujundaKuupaev(harjutuskord.getLopuaeg()));
        lopukellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getLopuaeg()));
        pikkusminutiteslahter.setText(String.valueOf(harjutuskord.getPikkusminutites()));
    }
    private void SalvestaHarjutus (){
        // Kui harjutuse nimi muudetud tühjaks siis anna harjutusele nimi
        String kirjeldus = harjutusekirjelduslahter.getText().toString();
        if (kirjeldus.isEmpty())
            harjutusekirjelduslahter.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));

        AndmedHarjutusse();
        if(mPPManager == null)
            mPPManager = new PilliPaevikDatabase(getApplicationContext());
        mPPManager.SalvestaHarjutusKord(getApplicationContext(),this.harjutuskord);
    }
    private void KustutaHarjutus(){
        mPPManager.KusututaHarjutus(this.teosid, this.harjutusid);
        Intent output = new Intent();
        setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_KUSTUTATUD), output);
        Log.d(this.getLocalClassName(), "Harjutuskord kustutatud : " + this.harjutusid);
    }

    public void MangiLugu(View v){
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mHeliFailDrive.getParcelFileDescriptor().getFileDescriptor());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(getLocalClassName(), "Viga mahamängimisel" + e.toString());
        }
    }

    private boolean AndmedHarjutuses(){
        return harjutuskord.getPikkussekundites() != 0 || !harjutuskord.getAlgusaeg().equals(harjutuskord.getLopuaeg());
    }
    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("KustutaHarjutus")) {
            Log.d(getLocalClassName(), "Kustutamine katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        } else {
            Log.e(this.getLocalClassName(), "kuiEiVastus. Tundmatust kohast tuldud !");
        }
    }
    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        if (dialog.getTag().equals("KustutaHarjutus")) {
            KustutaHarjutus();
            finish();
        } else {
            Log.e(this.getLocalClassName(), "kuiJahVastus. Tundmatust kohast tuldud !");
        }
    }

    private class AvaFailMangimiseks extends AsyncTask<String, Void, DriveContents> {

        @Override
        protected DriveContents doInBackground(String... driveIDs) {
            GoogleDriveUhendus mGDU = GoogleDriveUhendus.getInstance();
            mGDU.setmDriveActivity(null);
            DriveId dID = DriveId.decodeFromString(driveIDs[0]);
            DriveContents dFC = mGDU.AvaDriveFail(dID, DriveFile.MODE_READ_ONLY);
            return dFC;
        }

        protected void onPostExecute(DriveContents dFC) {

            if(dFC != null){
                mHeliFailDrive = dFC;
                Button mangilugu = (Button) findViewById(R.id.mangilugu);
                mangilugu.setVisibility(Button.VISIBLE);
            }

            super.onPostExecute(dFC);
        }

    }

}
