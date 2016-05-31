package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Intent;
import android.os.Bundle;


import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class HarjutusActivity extends AppCompatActivity implements AjaMuutuseTeavitus, LihtsaKusimuseKuulaja {

    private PilliPaevikDatabase mPPManager;
    private int teosid;
    private Teos teos;
    private int harjutusid;
    private HarjutusKord harjutuskord;
    private boolean bTehtudHarjutuseLoomine = false;

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
        setContentView(R.layout.activity_harjutus);

        harjutusekirjelduslahter = (EditText) findViewById(R.id.harjutusekirjeldus);
        alguskuupaevlahter = (TextView) findViewById(R.id.alguskuupaev);
        alguskellaaeglahter = (TextView) findViewById(R.id.alguskellaaeg);
        lopukuupaevlahter = (TextView) findViewById(R.id.lopukuupaev);
        lopukellaaeglahter = (TextView) findViewById(R.id.lopukellaaeg);
        pikkusminutiteslahter = (TextView) findViewById(R.id.pikkusminutites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.harjutus_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Log.d(getLocalClassName(), "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
            this.teosid = getIntent().getIntExtra("teos_id", 0);
            this.harjutusid = getIntent().getIntExtra("harjutus_id", 0);
        } else {
            Log.d(getLocalClassName(), "Loen saveInstants");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
            this.bTehtudHarjutuseLoomine = savedInstanceState.getBoolean("uusharjutus");
        }


        mPPManager = new PilliPaevikDatabase(getApplicationContext());
        this.teos = mPPManager.getTeos(this.teosid);
        HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getApplicationContext());
        this.harjutuskord = harjutuskorradmap.get(this.harjutusid);
        Log.d(getLocalClassName(), "Harjutus : " + this.harjutuskord);
        mAction.setTitle(this.teos.getNimi());

        if (this.harjutuskord == null && this.harjutusid == -1) {
            bTehtudHarjutuseLoomine = true;
            this.harjutuskord = new HarjutusKord(this.teosid);
            SalvestaAndmed();
            this.harjutusid = this.harjutuskord.getId();
        }
        AndmedHarjutuskorrastVaatele();
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            int result = 0;
            Intent intent = NavUtils.getParentActivityIntent(this);
            if (bTehtudHarjutuseLoomine) {
                result = getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_TEHTUD_LISATUD);
                Log.d("HarjutusActivity", "Result tagasi tehtud harjutus lisatud");

            } else {
                result = getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_MUUDA);
                Log.d("HarjutusActivity", "Result tagasi harjutus muudetud");
            }
            // Kui olemasolev harjutus ja nimi tühi siis anna harjutusele nimi
            String kirjeldus = harjutusekirjelduslahter.getText().toString();
            if (kirjeldus.isEmpty())
                harjutusekirjelduslahter.setText(getResources().getText(R.string.vaikimisisharjutusekirjeldus));

            AndmedHarjutusse();
            SalvestaAndmed();
            setResult(result, intent);
            Log.d(this.getLocalClassName(), "Harjutuskord : " + harjutuskord);
            NavUtils.navigateUpTo(this, intent);
            return true;
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

    void AndmedHarjutusse() {
        this.harjutuskord.setHarjutusekirjeldus(harjutusekirjelduslahter.getText().toString());
    }
    void AndmedHarjutuskorrastVaatele() {
        harjutusekirjelduslahter.setText(harjutuskord.getHarjutusekirjeldus());
        alguskuupaevlahter.setText(Tooriistad.KujundaKuupaev(harjutuskord.getAlgusaeg()));
        alguskellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getAlgusaeg()));
        lopukuupaevlahter.setText(Tooriistad.KujundaKuupaev(harjutuskord.getLopuaeg()));
        lopukellaaeglahter.setText(Tooriistad.KujundaKellaaeg(harjutuskord.getLopuaeg()));
        pikkusminutiteslahter.setText(String.valueOf(harjutuskord.getPikkusminutites()));
    }
    void SalvestaAndmed (){
        if(mPPManager == null)
            mPPManager = new PilliPaevikDatabase(getApplicationContext());
        mPPManager.SalvestaHarjutusKord(getApplicationContext(),this.harjutuskord);
    }

    // Abimeetod. Kasutusel meetodis MuudetudKuupaev
    private String AjaMuutusKeelatud(Date kuupaev) {
        String retVal = "";
        if (kuupaev.after(Calendar.getInstance().getTime()))
            retVal = "Uus aeg on tulevikus! Aega ei muudetud!";

        return retVal;
    }
    // Abimeetod. Kasutusel meetodis MuudetudKuupaev
    private HarjutusKord LooMuudetudAjagaHarjutusKord(Date kuupaev, boolean muudaalgust) {
        HarjutusKord retVal = new HarjutusKord();
        retVal.setAlgusaegEiArvuta(this.harjutuskord.getAlgusaeg());
        retVal.setLopuaegEiArvuta(this.harjutuskord.getLopuaeg());
        retVal.setPikkussekundites(this.harjutuskord.getPikkussekundites());
        if (muudaalgust) {
            retVal.setAlgusaeg(kuupaev);
        } else {
            retVal.setLopuaeg(kuupaev);
        }
        return retVal;
    }
    @Override
    public void AegMuudetud(Date kuupaev, boolean muudaalgust) {

        String VeaTeade = "";
        if (!(VeaTeade = AjaMuutusKeelatud(kuupaev)).isEmpty()) {
            Tooriistad.NaitaHoiatust(this, "", VeaTeade);
        } else {
            if (this.bTehtudHarjutuseLoomine == true) {
                if (muudaalgust) {
                    harjutuskord.setAlgusaeg(kuupaev);
                } else {
                    harjutuskord.setLopuaeg(kuupaev);
                }
                AndmedHarjutuskorrastVaatele();
                SalvestaAndmed();
            } else { // Hetkel siia kunagi ei jõuta sest kui on taastatud harjutus siis aegu ei lubata muuta
                HarjutusKord harjutusmuudetud = LooMuudetudAjagaHarjutusKord(kuupaev, muudaalgust);
                int praegunekestussekundid = harjutuskord.getPikkussekundites();
                int praegunekestusminutid = harjutuskord.getPikkusminutites();
                int muudetudkestusminutid = harjutusmuudetud.getPikkusminutites();
                if (muudetudkestusminutid < praegunekestusminutid) {
                    Tooriistad.NaitaHoiatust(this, "", "Uue algus- ja lõpuaja vahe (" +
                            muudetudkestusminutid + " min) on lühem kui praegune harjutuse kestus (" +
                            praegunekestusminutid + " min). Muutust ei tehtud. Kui soovid siiski muutust teha siis " +
                            "lühenda eelnevalt harjutuse kestust.");
                } else {
                    if (muudaalgust) {
                        harjutuskord.setAlgusaeg(kuupaev);
                    } else {
                        harjutuskord.setLopuaeg(kuupaev);
                    }
                    harjutuskord.setPikkussekundites(praegunekestussekundid);
                    AndmedHarjutuskorrastVaatele();
                    SalvestaAndmed();
                }
            }
        }

    }
    public void MuudaKuupaeva(View v) {

        // Salvesa harjutuse kirjeldus
        AndmedHarjutusse();
        if(bTehtudHarjutuseLoomine){
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
            muudaFragment.setArguments(args);
            muudaFragment.show(getSupportFragmentManager(), "Ajamuutus");
        }
    }
    public void MuudaPikkust(View v) {

        if(bTehtudHarjutuseLoomine){
            Bundle args = new Bundle();
            DialogFragment muudaKestustFragment = null;
            muudaKestustFragment = new ValiHarjutuseKestus();
            args.putInt("maksimum",this.harjutuskord.ArvutaPikkusMinutites());
            args.putInt("kestus", this.harjutuskord.getPikkusminutites());
            muudaKestustFragment.setArguments(args);
            muudaKestustFragment.show(getSupportFragmentManager(), "Kestusemuutus");
        }

    }

    // Dialoogi vastused
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if (dialog.getTag() == "KustutaHarjutus") {
            Log.d(getLocalClassName(), "Kustutamine katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        }
        if (dialog.getTag() == "Kestusemuutus") {
            Log.d(getLocalClassName(), "Kestuse muutus katkestatud:" + this.harjutusid + " Dialog :" + dialog.getTag());
        }
    }
    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        if (dialog.getTag() == "KustutaHarjutus") {
            mPPManager.KusututaHarjutus(this.teosid, this.harjutusid);
            Intent output = new Intent();
            setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_KUSTUTATUD), output);
            Log.d(this.getLocalClassName(), "Harjutuskord kustutatud : " + this.harjutusid);
            finish();
        }
        if (dialog.getTag() == "Kestusemuutus") {
            int uuskestus = dialog.getArguments().getInt("kestus");
            if(harjutuskord.getPikkusminutites() != uuskestus)
                this.harjutuskord.setPikkussekundites(uuskestus * 60);
            AndmedHarjutuskorrastVaatele();
            SalvestaAndmed();
            Log.d(getLocalClassName(), "Kestuse muutus, uus pikkus:" + uuskestus + " min. " + this.harjutusid + " Dialog :" + dialog.getTag());
        }
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("teos_id", this.teosid);
        savedInstanceState.putInt("harjutus_id", this.harjutusid);
        savedInstanceState.putBoolean("uusharjutus", this.bTehtudHarjutuseLoomine);

        Log.d(getLocalClassName(), "Salvestan :" + this.teosid + " " + this.harjutusid);

        super.onSaveInstanceState(savedInstanceState);
    }

}
