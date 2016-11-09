package com.vaskjala.vesiroosi20.pillipaevik;


import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

public class TeosActivity extends AppCompatActivity implements TeosFragmendiKuulaja  {

    private int teosid;
    private int itemposition;
    private boolean bUueTeoseLoomine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.teos_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Instance ei ole salvestatud, loen Intent obektist");
            this.teosid = getIntent().getIntExtra("item_id", 0);
            this.itemposition = getIntent().getIntExtra("item_position", 0);
        } else {
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Loen saveinstantsist");
            this.teosid = savedInstanceState.getInt("teoseid");
            this.itemposition = savedInstanceState.getInt("item_position");
            this.bUueTeoseLoomine = savedInstanceState.getBoolean("uusteos");
        }
        if ( this.teosid == -1 ) {
            Log.d("TeosActivity", "Uus teos");
            bUueTeoseLoomine = true;
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("teoseid", this.teosid);
        savedInstanceState.putInt("item_position", this.itemposition);
        savedInstanceState.putBoolean("uusteos", this.bUueTeoseLoomine);

        if(BuildConfig.DEBUG) Log.d("TeosActivity", "onSaveInstanceState :" + this.teosid + " " + this.itemposition );

        super.onSaveInstanceState(savedInstanceState);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if(BuildConfig.DEBUG) Log.d("TeosActivity","Menüü valik vajutatud");
        if(item.getItemId() == android.R.id.home){
            if(BuildConfig.DEBUG) Log.d("TeosActivity", "Up nuppu vajutatud");
            NavUtils.navigateUpTo(this, SeadistaLahkumine());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        SeadistaLahkumine();
        finish();
        super.onBackPressed();
    }

    private Intent SeadistaLahkumine(){
        Intent intent = NavUtils.getParentActivityIntent(this);
        int result;
        if(this.bUueTeoseLoomine) {
            result = getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_LISATUD);
        } else {
            result = getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_MUUDETUD);
        }
        intent.putExtra("item_position", this.itemposition);
        intent.putExtra("item_id", this.teosid);
        setResult(result, intent);
        return intent;
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "onActivityResult");
        TeosFragment teosFragment = (TeosFragment) getFragmentManager().findFragmentById(R.id.teosfragment);
        teosFragment.VarskendaHarjutusteJaStatistika();
        if(data != null) {
            int kustutamisealge = data.getIntExtra("kustutamisealge", 0);
            if (kustutamisealge == Tooriistad.TUHIHARJUTUS_KUSTUTA)
                Tooriistad.KuvaAutomaatseKustutamiseTeade(this);
        }
    }


    @Override
    public void HarjutusValitud(int teosid, int harjutusid) {
        Intent intent = new Intent(this, HarjutusMuudaActivity.class);
        intent.putExtra("teos_id", teosid);
        intent.putExtra("harjutus_id", harjutusid);
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Avan olemasolevat harjutust. Teosid : " + teosid +
                " Harjutus:" + harjutusid);
        startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_MUUDA));
    }
    @Override
    public void AlustaHarjutust(int teosid) {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Alusta uut harjutust");
        Intent intent = new Intent(this, HarjutusUusActivity.class);
        intent.putExtra("teos_id", teosid);
        intent.putExtra("harjutus_id", -1);
        startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_UUS));
    }
    @Override
    public void LisaTehtudHarjutus(int teosid) {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Lisa tehtud harjutus");
        Intent intent = new Intent(this, HarjutusLisaTehtudActivity.class);
        intent.putExtra("teos_id", teosid);
        intent.putExtra("harjutus_id", -1);
        startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_TEHTUD ));
    }

    @Override
    public void KustutaTeos(Teos teos, int itemposition) {
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Tagasi kustutamisega. Pos:" + this.itemposition);
        Intent output = new Intent();
        output.putExtra("item_position", this.itemposition);
        setResult(getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD), output);
        finish();
    }

    @Override
    public void VarskendaTeosList() {

    }

    @Override
    public void VarskendaTeosFragment(Teos teos) {

    }

    @Override
    public void VarskendaTeosListiElement(Teos teos) {

    }

    @Override
    public void SeaTeosid(int teosid) {

    }
}
