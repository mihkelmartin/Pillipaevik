package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.gms.common.GoogleApiAvailability;

public class SeadedActivity extends AppCompatActivity {

    private EditText minueesnimi;
    private EditText minuperenimi;
    private EditText minuinstrument;
    private EditText minuepost;

    private EditText muusikakool;
    private EditText klass;

    private EditText opetajaeesnimi;
    private EditText opetajaperenimi;
    private EditText opetajaepost;

    private EditText paevasharjutada;

    private EditText googlekonto;
    private Switch kasLubadaMikrofonigaSalvestamine;
    private Switch kaskasutadagoogledrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seaded);
        Toolbar toolbar = (Toolbar) findViewById(R.id.seaded_toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(R.string.seaded_tiitel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AlustaAtribuudid();
        TaastaAndmed();
        PaneFookusePassija();

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(),"Salvestan ja Up");
            SalvestaAndmed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(),"Salvestan ja OnBackPressed");
        SalvestaAndmed();
        super.onBackPressed();
    }

    private void AlustaAtribuudid(){
        minueesnimi = ((EditText)findViewById(R.id.minueesnimi));
        minuperenimi = ((EditText)findViewById(R.id.minuperenimi));
        minuinstrument = ((EditText)findViewById(R.id.minuinstrument));
        minuepost = ((EditText)findViewById(R.id.minuepost));
        muusikakool = ((EditText)findViewById(R.id.muusikakool));
        klass = ((EditText)findViewById(R.id.klass));
        opetajaeesnimi = ((EditText)findViewById(R.id.opetajaeesnimi));
        opetajaperenimi = ((EditText)findViewById(R.id.opetajaperenimi));
        opetajaepost = ((EditText)findViewById(R.id.opetajaepost));
        paevasharjutada = ((EditText)findViewById(R.id.paevasharjutada));
        googlekonto = ((EditText)findViewById(R.id.googlekonto));
        kasLubadaMikrofonigaSalvestamine = ((Switch) findViewById(R.id.kasLubadaMikrofonigaSalvestamine));
        kaskasutadagoogledrive = ((Switch) findViewById(R.id.kasKasutadaGoogleDrive));

    }
    private void PaneFookusePassija(){
        SeadedFookusePassija mFP = new SeadedFookusePassija();
        minueesnimi.setOnFocusChangeListener(mFP);
        minuperenimi.setOnFocusChangeListener(mFP);
        minuinstrument.setOnFocusChangeListener(mFP);
        minuepost.setOnFocusChangeListener(mFP);
        muusikakool.setOnFocusChangeListener(mFP);
        klass.setOnFocusChangeListener(mFP);
        opetajaeesnimi.setOnFocusChangeListener(mFP);
        opetajaperenimi.setOnFocusChangeListener(mFP);
        opetajaepost.setOnFocusChangeListener(mFP);
        paevasharjutada.setOnFocusChangeListener(mFP);
        kasLubadaMikrofonigaSalvestamine.setOnFocusChangeListener(mFP);
        kaskasutadagoogledrive.setOnFocusChangeListener(mFP);
    }
    private void TaastaAndmed(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        minueesnimi.setText(sharedPref.getString("minueesnimi", ""));
        minuperenimi.setText(sharedPref.getString("minuperenimi", ""));
        minuinstrument.setText(sharedPref.getString("minuinstrument", ""));
        minuepost.setText(sharedPref.getString("minuepost", ""));
        muusikakool.setText(sharedPref.getString("muusikakool", ""));
        klass.setText(sharedPref.getString("klass", ""));
        opetajaeesnimi.setText(sharedPref.getString("opetajaeesnimi", ""));
        opetajaperenimi.setText(sharedPref.getString("opetajaperenimi", ""));
        opetajaepost.setText(sharedPref.getString("opetajaepost", ""));

        String lszPaevas = String.valueOf(sharedPref.getInt("paevasharjutada", 0));
        if(lszPaevas.equals("0"))
            lszPaevas = "";
        paevasharjutada.setText(lszPaevas);
        googlekonto.setText(sharedPref.getString("googlekonto", ""));
        kasLubadaMikrofonigaSalvestamine.setChecked(sharedPref.getBoolean("kaslubadamikrofonigasalvestamine", true));
        kaskasutadagoogledrive.setChecked(sharedPref.getBoolean("kaskasutadagoogledrive", true));

    }
    private void SalvestaAndmed(){

        if(BuildConfig.DEBUG) Log.d(getLocalClassName(),"Salvestan");
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.seadete_fail), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("minueesnimi", minueesnimi.getText().toString());
        editor.putString("minuperenimi", minuperenimi.getText().toString());
        editor.putString("minuinstrument", minuinstrument.getText().toString());
        editor.putString("minuepost", minuepost.getText().toString());

        editor.putString("muusikakool", muusikakool.getText().toString());
        editor.putString("klass", klass.getText().toString());

        editor.putString("opetajaeesnimi", opetajaeesnimi.getText().toString());
        editor.putString("opetajaperenimi", opetajaperenimi.getText().toString());
        editor.putString("opetajaepost", opetajaepost.getText().toString());

        String minutid = paevasharjutada.getText().toString();
        if(!minutid.isEmpty())
            editor.putInt("paevasharjutada", Integer.parseInt(minutid));
        else
            editor.putInt("paevasharjutada", 0);
        editor.putString("googlekonto", googlekonto.getText().toString());
        editor.putBoolean("kaslubadamikrofonigasalvestamine", kasLubadaMikrofonigaSalvestamine.isChecked());
        editor.putBoolean("kaskasutadagoogledrive", kaskasutadagoogledrive.isChecked());
        if(kaskasutadagoogledrive.isChecked())
            editor.putString("googlekonto", googlekonto.getText().toString());
        else
            editor.putString("googlekonto", "");

        editor.commit();
        BackupManager backupManager = new BackupManager(this);
        backupManager.dataChanged();
    }
    private class SeadedFookusePassija implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus) {
            SalvestaAndmed();
        }
    }

    public void KasSalvestameMikrofoniga(View v){
        if(!kasLubadaMikrofonigaSalvestamine.isChecked()){
            kaskasutadagoogledrive.setChecked(false);
        }
    }
    public void KasKasutameGoogleDrive(View v){
        if(kaskasutadagoogledrive.isChecked()){
            kasLubadaMikrofonigaSalvestamine.setChecked(true);
        }
    }
}
