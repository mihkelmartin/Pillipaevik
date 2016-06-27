package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.*;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class HarjutusLisaTehtudActivity extends AppCompatActivity implements AjaMuutuseTeavitus, LihtsaKusimuseKuulaja {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutuslisatehtud);

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
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
            this.teosid = getIntent().getIntExtra("teos_id", 0);
            this.harjutusid = getIntent().getIntExtra("harjutus_id", 0);
        } else {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Loen saveInstants");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
        }


        mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getApplicationContext());
        this.harjutuskord = harjutuskorradmap.get(this.harjutusid);
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Harjutus : " + this.harjutuskord);
        mAction.setTitle(teos.getNimi());

        if (this.harjutuskord == null && this.harjutusid == -1) {
            this.harjutuskord = new HarjutusKord(this.teosid);
            SalvestaHarjutus();
            this.harjutusid = this.harjutuskord.getId();
        }
        AndmedHarjutuskorrastVaatele();
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("teos_id", this.teosid);
        savedInstanceState.putInt("harjutus_id", this.harjutusid);
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onSaveInstanceState " + this.teosid + " " + this.harjutusid);

        super.onSaveInstanceState(savedInstanceState);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent output = new Intent();
            if(AndmedHarjutuses()){
                // Kui harjutuse nimi muudetud tühjaks siis anna harjutusele nimi
                String kirjeldus = harjutusekirjelduslahter.getText().toString();
                if (kirjeldus.isEmpty())
                    harjutusekirjelduslahter.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));
                SalvestaHarjutus();
                setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_TEHTUD_LISATUD), output);
                if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Harjutus lisatud: " + harjutuskord);
            } else {
                if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Uus harjutus tühi: " + harjutuskord);
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

    public void onBackPressed() {

        if(AndmedHarjutuses()) {
            // Kui harjutuse nimi muudetud tühjaks siis anna harjutusele nimi
            String kirjeldus = harjutusekirjelduslahter.getText().toString();
            if (kirjeldus.isEmpty())
                harjutusekirjelduslahter.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));
            SalvestaHarjutus();
            Intent output = new Intent();
            setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_TEHTUD_LISATUD), output);
            if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Harjutuskord : " + this.harjutuskord.toString());
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

    // Abimeetod. Kasutusel meetodis MuudetudKuupaev
    private String AjaMuutusKeelatud(Date kuupaev) {
        String retVal = "";
        if (kuupaev.after(Calendar.getInstance().getTime()))
            retVal = "Uus aeg on tulevikus! Aega ei muudetud!";

        return retVal;
    }

    @Override
    public void AegMuudetud(Date kuupaev, boolean muudaalgust) {

        String VeaTeade;
        if (!(VeaTeade = AjaMuutusKeelatud(kuupaev)).isEmpty()) {
            Tooriistad.NaitaHoiatust(this, "", VeaTeade);
        } else {
            if (muudaalgust) {
                harjutuskord.setAlgusaeg(kuupaev);
            } else {
                harjutuskord.setLopuaeg(kuupaev);
            }
            AndmedHarjutuskorrastVaatele();
            SalvestaHarjutus();
        }

    }
    public void MuudaKuupaeva(View v) {

        // Salvesta harjutuse kirjeldus
        AndmedHarjutusse();

        Bundle args = new Bundle();
        DialogFragment muudaFragment = null;
        switch (v.getId()) {
            case R.id.alguskuupaev:
                args.putBoolean("muudaalgust", true);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getAlgusaeg()));
                muudaFragment = new ValiKuupaev();
                break;
            case R.id.lopukuupaev:
                args.putBoolean("muudaalgust", false);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getLopuaeg()));
                muudaFragment = new ValiKuupaev();
                break;
            case R.id.alguskellaaeg:
                args.putBoolean("muudaalgust", true);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getAlgusaeg()));
                muudaFragment = new ValiKellaaeg();
                break;
            case R.id.lopukellaaeg:
                args.putBoolean("muudaalgust", false);
                args.putString("datetime", Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getLopuaeg()));
                muudaFragment = new ValiKellaaeg();
                break;
            default:
                break;
        }
        assert muudaFragment != null;
        muudaFragment.setArguments(args);
        muudaFragment.show(getSupportFragmentManager(), "Ajamuutus");

    }
    public void MuudaPikkust(View v) {

        Bundle args = new Bundle();
        DialogFragment muudaKestustFragment = new ValiHarjutuseKestus();
        args.putInt("maksimum",this.harjutuskord.ArvutaPikkusMinutites());
        args.putInt("kestus", this.harjutuskord.getPikkusminutites());
        // Kui nimi on antud siis tuleb see harjutuskord objekti viia sest
        // kui tagasi tullakse siis viikse andmed objektist vaatele
        AndmedHarjutusse();
        muudaKestustFragment.setArguments(args);
        muudaKestustFragment.show(getSupportFragmentManager(), "Kestusemuutus");

    }

    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {

        if (dialog.getTag().equals("KustutaHarjutus")) {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Kustutamine katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        } else if (dialog.getTag().equals("Kestusemuutus")) {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Kestuse muutus katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        } else {
            if(BuildConfig.DEBUG) Log.e(this.getLocalClassName(), "kuiEiVastus. Tundmatust kohast tuldud !");
        }

    }

    @Override
    public void kuiJahVastus(DialogFragment dialog) {

        if (dialog.getTag().equals("KustutaHarjutus")) {
            KustutaHarjutus();
            finish();
        } else  if (dialog.getTag().equals("Kestusemuutus")) {
            int uuskestus = dialog.getArguments().getInt("kestus");
            if(harjutuskord.getPikkusminutites() != uuskestus)
                this.harjutuskord.setPikkussekundites(uuskestus * 60);
            AndmedHarjutuskorrastVaatele();
            SalvestaHarjutus();
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Kestuse muutus, uus pikkus:" + uuskestus + " min. " + this.harjutusid + " Dialog :" + dialog.getTag());
        } else {
            if(BuildConfig.DEBUG) Log.e(this.getLocalClassName(), "kuiJahVastus. Tundmatust kohast tuldud !");
        }

    }

}
