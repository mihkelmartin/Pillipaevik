package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class HarjutusUusActivity extends AppCompatActivity implements YldineKysimuseAken.NoticeDialogListener {

    private PilliPaevikDatabase mPPManager;
    private int teosid;
    private int harjutusid;
    private HarjutusKord harjutus;
    private boolean taimertootab = false;
    private long stardiaeg = 0;
    private long kulunudaeg = 0;
    private static final short viiv = 300;

    @Override
    protected void onStop() {
        Log.d("HarjutusUusActivity", "On Stop");
        if(taimertootab)
            handler.removeCallbacks(runnable);
        super.onStop();
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

    @Override
    protected void onStart() {
        if(taimertootab)
            handler.postDelayed(runnable, viiv);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutus_uus);

        Toolbar toolbar = (Toolbar) findViewById(R.id.uusharjutus_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayHomeAsUpEnabled(true);

        this.teosid = getIntent().getIntExtra("teos_id", 0);
        this.harjutusid = getIntent().getIntExtra("harjutusid", 0);
        Log.d(this.getLocalClassName(), "Teos : " + this.teosid + " Harjutus : " + this.harjutusid);

        mPPManager = new PilliPaevikDatabase(getApplicationContext());

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

            Teos teos = mPPManager.getTeos(this.teosid);
            HashMap<Integer, HarjutusKord> harjutuskorradmap  = teos.getHarjutuskorradmap(getApplicationContext());
            this.harjutus = harjutuskorradmap.get(this.harjutusid);
            Log.d(this.getLocalClassName(), "Harjutus taastatud teose kaudu : " + this.harjutusid);

            if(taimertootab)
                ((Button)findViewById(R.id.kaivitataimernupp)).setText("Katkesta");
            else
                ((Button)findViewById(R.id.kaivitataimernupp)).setText("Jätka");

            // Taimer on pausil, kuid on juba lugenud aega
            if(!taimertootab && kulunudaeg != 0) {
                ((TextView) findViewById(R.id.timer)).setText(String.valueOf(Tooriistad.formatElapsedTime(kulunudaeg)));
            }
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        if(item.getItemId() == android.R.id.home){
            if(taimertootab) {
                taimertootab = false;
                kulunudaeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;
                harjutus.setLopuaegEiArvuta(Calendar.getInstance().getTime());
                harjutus.setPikkussekundites((int) (kulunudaeg / 1000));
                handler.removeCallbacks(runnable);
            }
            EditText ETharjutusekirjeldus = (EditText) findViewById(R.id.harjutusekirjeldus);
            String kirjeldus = ETharjutusekirjeldus.getText().toString();
            if(kirjeldus.isEmpty())
                ETharjutusekirjeldus.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));

            AndmedHarjutusse(this.harjutus);
            mPPManager.SalvestaHarjutusKord(getApplicationContext(), this.harjutus);
            Intent output = new Intent();
            setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_UUS_LISATUD), output);
            Log.d(this.getLocalClassName(), "Harjutuskord : " + this.harjutus.toString());
            finish();
        }
        if(item.getItemId() == R.id.kustutaharjutus){
            Bundle args = new Bundle();
            args.putString("kysimus","Kustutad Harjutuse ?");
            args.putString("jahvastus","Jah");
            args.putString("eivastus","Ei");
            DialogFragment newFragment = new YldineKysimuseAken();
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

    void AndmedHarjutusse (HarjutusKord harjutus){
        String kirjeldus = ((EditText)findViewById(R.id.harjutusekirjeldus)).getText().toString();
        harjutus.setHarjutusekirjeldus(kirjeldus);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long aeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;
            ((TextView) findViewById(R.id.timer)).setText(String.valueOf( Tooriistad.formatElapsedTime(aeg)));
            handler.postDelayed(this, viiv);
        }
    };
    public void KaivitaTaimer(View view){
        if(taimertootab) {
            taimertootab = false;
            kulunudaeg = kulunudaeg + System.currentTimeMillis() - stardiaeg;

            harjutus.setPikkussekundites((int)(kulunudaeg / 1000));
            int kulunudminuteid = harjutus.getPikkusminutites();
            Calendar c = Calendar.getInstance();
            c.setTime(harjutus.getAlgusaeg());
            c.add(Calendar.MINUTE,kulunudminuteid);
            harjutus.setLopuaegEiArvuta(c.getTime());
            mPPManager.SalvestaHarjutusKord(getApplicationContext(), this.harjutus);

            ((Button)findViewById(R.id.kaivitataimernupp)).setText("Jätka");
            handler.removeCallbacks(runnable);
        } else {
            // Esimene start
            if(stardiaeg == 0) {
                harjutus.setAlgusaeg(Tooriistad.HetkeKuupaevNullitudSekunditega());
                mPPManager.SalvestaHarjutusKord(getApplicationContext(), this.harjutus);
            }
            taimertootab = true;
            this.stardiaeg = System.currentTimeMillis();
            ((Button)findViewById(R.id.kaivitataimernupp)).setText("Katkesta");
            handler.postDelayed(runnable, viiv);
        }
    }


    // Dialoogi vastused
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d("TeosActivity", "Kustutamine katkestatud:" + this.teosid);
    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mPPManager.KusututaHarjutus(this.teosid, this.harjutusid);
        Log.d(this.getLocalClassName(), "Uus harjutuskord kustutatud : " + this.harjutusid);
        Intent output = new Intent();
        setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_UUS_LOOMATA), output);
        finish();
    }

}
