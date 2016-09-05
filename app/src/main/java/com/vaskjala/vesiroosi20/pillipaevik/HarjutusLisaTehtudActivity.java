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


public class HarjutusLisaTehtudActivity extends AppCompatActivity implements HarjutusLisaTehtudFragmendiKuulaja{

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
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Teos : " + this.teosid + " Harjutus:" + this.harjutusid);
            this.teosid = getIntent().getIntExtra("teos_id", 0);
            this.harjutusid = getIntent().getIntExtra("harjutus_id", 0);
        } else {
            if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Loen saveInstants");
            this.teosid = savedInstanceState.getInt("teos_id");
            this.harjutusid = savedInstanceState.getInt("harjutus_id");
        }
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
    public void KustutaHarjutus(int harjutusid) {
        finish();
    }
}
