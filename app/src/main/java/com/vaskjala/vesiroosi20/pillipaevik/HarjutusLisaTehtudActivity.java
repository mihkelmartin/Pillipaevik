package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;


public class HarjutusLisaTehtudActivity extends AppCompatActivity implements HarjutusFragmendiKuulaja{

    private int teosid;
    private int harjutusid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutuslisatehtud);

        Toolbar toolbar = (Toolbar) findViewById(R.id.harjutus_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        assert mAction != null;
        mAction.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            this.teosid = getIntent().getIntExtra("teos_id", 0);
            this.harjutusid = getIntent().getIntExtra("harjutus_id", 0);
        } else {
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
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
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "onSaveInstanceState " + this.teosid + " " + this.harjutusid);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            HarjutusLisaTehtudFragment harjutusLisaTehtudFragment= (HarjutusLisaTehtudFragment) getFragmentManager().findFragmentById(R.id.harjutuslisatehtudfragment);
            harjutusLisaTehtudFragment.SuleHarjutus();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        HarjutusLisaTehtudFragment harjutusLisaTehtudFragment= (HarjutusLisaTehtudFragment) getFragmentManager().findFragmentById(R.id.harjutuslisatehtudfragment);
        harjutusLisaTehtudFragment.SuleHarjutus();
        super.onBackPressed();
    }

    @Override
    public void HarjutusLisatud(int teosid, int harjutusid) {

    }

    @Override
    public void HarjutusKustutatud(int teosid, int harjutusid, int itemposition, int kustutamisealge) {
        Intent intent = new Intent();
        intent.putExtra("harjutus_id", this.harjutusid);
        intent.putExtra("kustutamisealge", kustutamisealge);
        setResult(0, intent);
        finish();
    }

    @Override
    public void HarjutusMuudetud(int teosid, int harjutusid, int itemposition) {

    }

    @Override
    public void SeaHarjutusid(int harjutuseid) {

    }
}
