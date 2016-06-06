package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Intent;
import android.content.IntentSender;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.util.Calendar;
import java.util.HashMap;


public class HarjutusUusActivity extends AppCompatActivity implements LihtsaKusimuseKuulaja,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks  {

    private PilliPaevikDatabase mPPManager;
    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutus;
    private boolean taimertootab = false;
    private long stardiaeg = 0;
    private long kulunudaeg = 0;
    private static final short viiv = 300;

    private static TextView timer;
    private static CheckBox kasSalvestame;
    private static Button kaivitaTimerNupp;

    private MediaRecorder mRecorder = null;

    private static GoogleApiClient mGoogleApiClient;

    protected void onStart() {
        if(taimertootab)
            handler.postDelayed(runnable, viiv);

        //GoogleKettaYhendus();
        super.onStart();
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("harjutusid", this.harjutusid);
        savedInstanceState.putLong("stardiaeg", this.stardiaeg);
        savedInstanceState.putLong("kulunudaeg", this.kulunudaeg);
        savedInstanceState.putBoolean("taimertootab", this.taimertootab);
        Log.d("HarjutusUusActivity", "Salvestan :" + this.harjutusid + " " + this.stardiaeg + " " + this.kulunudaeg + " Taimer sees:" +this.taimertootab);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    protected void onStop() {
        Log.d("HarjutusUusActivity", "On Stop");
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
        Log.d(this.getLocalClassName(), "Teos : " + this.teosid + " Harjutus : " + this.harjutusid);

        mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        mAction.setTitle(teos.getNimi());

        timer = (TextView) findViewById(R.id.timer);
        kasSalvestame = (CheckBox) findViewById(R.id.KasSalvestame);
        kaivitaTimerNupp = (Button) findViewById(R.id.kaivitataimernupp);

        if (savedInstanceState == null) {
            this.harjutus = new HarjutusKord(this.teosid);
            mPPManager.SalvestaHarjutusKord(getApplicationContext(), this.harjutus);
            this.harjutusid = this.harjutus.getId();
            Log.d(this.getLocalClassName(), "Uus harjutus loodud : " + this.harjutusid);
        }else {
            this.harjutusid = savedInstanceState.getInt("harjutusid");
            this.stardiaeg = savedInstanceState.getLong("stardiaeg");
            this.kulunudaeg = savedInstanceState.getLong("kulunudaeg");
            this.taimertootab = savedInstanceState.getBoolean("taimertootab");
            Log.d(this.getLocalClassName(), "Loen savedinstantsist :" + this.harjutusid + " " +
                    this.stardiaeg + " " + this.kulunudaeg + " Taimer sees:" +this.taimertootab);

            HashMap<Integer, HarjutusKord> harjutuskorradmap  = teos.getHarjutuskorradmap(getApplicationContext());
            this.harjutus = harjutuskorradmap.get(this.harjutusid);
            Log.d(this.getLocalClassName(), "Harjutus taastatud teose kaudu : " + this.harjutusid);

            if(taimertootab)
                kaivitaTimerNupp.setText("Katkesta");
            else
                kaivitaTimerNupp.setText("Jätka");

            // Taimer on pausil, kuid on juba lugenud aega
            if(!taimertootab && kulunudaeg != 0) {
                timer.setText(String.valueOf(Tooriistad.formatElapsedTime(kulunudaeg)));
            }
        }
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
                Log.d(this.getLocalClassName(), "Harjutuskord : " + this.harjutus.toString());
            } else {
                KustutaHarjutus();
            }
            finish();
        }
        if(item.getItemId() == R.id.kustutaharjutus){
            Bundle args = new Bundle();
            args.putString("kysimus","Kustutad Harjutuse ?");
            args.putString("jahvastus","Jah");
            args.putString("eivastus","Ei");
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
            Log.d(this.getLocalClassName(), "Harjutuskord : " + this.harjutus.toString());
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
        mPPManager.SalvestaHarjutusKord(getApplicationContext(), this.harjutus);
    }
    private void KustutaHarjutus(){
        mPPManager.KusututaHarjutus(this.teosid, this.harjutusid);
        Log.d(this.getLocalClassName(), "Uus harjutuskord kustutatud : " + this.harjutusid);
        Intent output = new Intent();
        setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_UUS_LOOMATA), output);
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long aeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;
            timer.setText(String.valueOf( Tooriistad.formatElapsedTime(aeg)));
            handler.postDelayed(this, viiv);
        }
    };
    public void KaivitaTaimer(View v){

        if(taimertootab) {
            SeisataLindistaja();
            SeisataTaimer();
            mPPManager.SalvestaHarjutusKord(getApplicationContext(), this.harjutus);
            kaivitaTimerNupp.setText("Jätka");
        } else {
            KaivitaLindistaja();
            KaivitaTaimer();
            kaivitaTimerNupp.setText("Katkesta");
        }
    }

    private void KaivitaTaimer(){
        if(stardiaeg == 0) {
            harjutus.setAlgusaeg(Tooriistad.HetkeKuupaevNullitudSekunditega());
            mPPManager.SalvestaHarjutusKord(getApplicationContext(), this.harjutus);
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
        if(kasSalvestame.isChecked()) {
            harjutus.setHelifail(getFilesDir().getPath().toString() + "/" + harjutus.MoodustaFailiNimi());
            mRecorder = new MediaRecorder();
            Log.e(getLocalClassName(), "Fail:" + harjutus.getHelifail());

            try {
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setOutputFile(harjutus.getHelifail());
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.prepare();
                mRecorder.start();
            } catch (Exception e) {
                mRecorder = null;
                Log.e(getLocalClassName(), "prepare() failed" + e.toString());
            }
        }
    }
    private void SeisataLindistaja() {
        Log.d(getLocalClassName(), "Lõpetan lindistamise");
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            Log.d(getLocalClassName(), "Lõpetasin lindistamise");
        }
    }
    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        Log.d("TeosActivity", "Kustutamine katkestatud:" + this.teosid);
    }
    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        KustutaHarjutus();
        finish();
    }



    private boolean kuiAndmedHarjutuses(){
        return harjutus.getPikkussekundites() != 0 || !harjutus.getAlgusaeg().equals(harjutus.getLopuaeg());
    }


    // Google Drive
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 9999:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    private void GoogleKettaYhendus(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // TODO
                connectionResult.startResolutionForResult(this, 9999);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }
    @Override
    public void onConnectionSuspended(int i) {

    }
}
