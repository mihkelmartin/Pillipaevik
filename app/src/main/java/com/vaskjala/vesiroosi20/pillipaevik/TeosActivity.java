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
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtneKusimus;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;

public class TeosActivity extends AppCompatActivity implements LihtsaKusimuseKuulaja, TeosFragmendiKuulaja  {

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
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        TeosFragment teosFragment = (TeosFragment) getFragmentManager().findFragmentById(R.id.teosfragment);
        teosFragment.VarskendaHarjutusteList();
    }


    @Override
    public void HarjutusValitud(int teosid, int harjutusid) {
        Intent intent = new Intent(this, HarjutusMuudaActivity.class);
        intent.putExtra("teos_id", teosid);
        intent.putExtra("harjutus_id", harjutusid);
        if(BuildConfig.DEBUG) Log.d("TeosFragment", "Avan olemasolevat harjutust. Teosid : " + teosid +
                " Harjutus:" + harjutusid);
        startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_MUUDA));
    }
    @Override
    public void AlustaHarjutust(int teosid) {
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Alusta uut harjutust");
        Intent intent = new Intent(this, HarjutusUusActivity.class);
        intent.putExtra("teos_id", teosid);
        intent.putExtra("harjutusid", -1);
        startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_UUS));
    }
    @Override
    public void LisaTehtudHarjutus(int teosid) {
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Lisa tehtud harjutus");
        Intent intent = new Intent(this, HarjutusLisaTehtudActivity.class);
        intent.putExtra("teos_id", teosid);
        intent.putExtra("harjutus_id", -1);
        startActivityForResult(intent, getResources().getInteger(R.integer.TEOS_ACTIVITY_INTENT_HARJUTUS_TEHTUD ));
    }

    @Override
    public void KustutaTeos(int teosid) {
        Bundle args = new Bundle();
        args.putString("pealkiri",getString(R.string.dialog_kas_kustuta_teose_pealkiri));
        args.putString("kysimus",getString(R.string.dialog_kas_kustuta_teose_kusimus));
        args.putString("jahvastus",getString(R.string.jah));
        args.putString("eivastus",getString(R.string.ei));
        DialogFragment newFragment = new LihtneKusimus();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "Kustuta teos");
    }

    // Dialoogi vastused kustutamise korral
    @Override
    public void kuiEiVastus(DialogFragment dialog) {
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Kustutamine katkestatud:" + this.teosid);
    }

    @Override
    public void kuiJahVastus(DialogFragment dialog) {
        TeosFragment teosFragment = (TeosFragment) getFragmentManager().findFragmentById(R.id.teosfragment);
        teosFragment.KustutaTeos();
        Intent output = new Intent();
        output.putExtra("item_position", this.itemposition);
        if(BuildConfig.DEBUG) Log.d("TeosActivity", "Tagasi kustutamisega. Pos:" + this.itemposition);
        setResult(getResources().getInteger(R.integer.TEOS_ACTIVITY_RETURN_KUSTUTATUD), output);
        finish();
    }
}
