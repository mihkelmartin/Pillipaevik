package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.vaskjala.vesiroosi20.pillipaevik.teenused.*;


public class HarjutusMuudaActivity extends AppCompatActivity implements HarjutusFragmendiKuulaja {

    private int teosid;
    private int harjutusid;
    private int itemposition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutusmuuda);

        Toolbar toolbar = (Toolbar) findViewById(R.id.harjutus_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        assert mAction != null;
        mAction.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            this.teosid = getIntent().getIntExtra("teos_id", 0);
            this.harjutusid = getIntent().getIntExtra("harjutus_id", 0);
            this.itemposition = getIntent().getIntExtra("item_position", 0);
        } else {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Loen savedInstanceState");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
            this.itemposition = savedInstanceState.getInt("item_position");
        }
        if(BuildConfig.DEBUG) Log.d(this.getLocalClassName(), "Teos : " + this.teosid + " Harjutus : " + this.harjutusid);
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPPManager.getTeos(this.teosid);
        mAction.setTitle(teos.getNimi());
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("teos_id", this.teosid);
        savedInstanceState.putInt("harjutus_id", this.harjutusid);
        savedInstanceState.putInt("item_position", this.itemposition);
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onSaveInstanceState: " + this.teosid + " " + this.harjutusid);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SeadistaLahkumine();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        SeadistaLahkumine();
        super.onBackPressed();
    }

    private void SeadistaLahkumine(){
        Intent intent = new Intent();
        intent.putExtra("item_position", itemposition);
        setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_MUUDA), intent);
        HarjutusMuudaFragment harjutusMuudaFragment =
                (HarjutusMuudaFragment) getFragmentManager().findFragmentById(R.id.harjutusmuudafragment);
        harjutusMuudaFragment.SuleHarjutus();
    }

    @Override
    public void HarjutusLisatud(int teosid, int harjutusid) {

    }

    @Override
    public void HarjutusKustutatud(int teosid, int harjutusid, int itemposition) {
        Intent intent = new Intent();
        intent.putExtra("item_position", itemposition);
        setResult(getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_KUSTUTATUD), intent);
        finish();
    }
    @Override
    public void HarjutusMuudetud(int teosid, int harjutusid, int position) {

    }

    @Override
    public void SeaHarjutusid(int harjutuseid) {

    }
}
