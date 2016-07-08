package com.vaskjala.vesiroosi20.pillipaevik.kalender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.R;

import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mihkel on 11.06.2016.
 */
public class HarjutusteKalenderActivity extends AppCompatActivity {

    private HarjutusKalenderPaevadRecyclerViewAdapter mMainAdapter = null;
    List<KalendriKirje> pPaevadeList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutuste_kalender);

        Toolbar toolbar = (Toolbar) findViewById(R.id.kalender_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayHomeAsUpEnabled(true);

        pPaevadeList = new ArrayList<KalendriKirje>();
        Tooriistad.LooKuupaevad(getApplicationContext(), 60, pPaevadeList);

        RecyclerView mKalendriTabel = (RecyclerView) findViewById(R.id.kalendri_tabel);
        mMainAdapter = new HarjutusKalenderPaevadRecyclerViewAdapter(pPaevadeList);
        mKalendriTabel.setAdapter(mMainAdapter);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int itemposition = data.getIntExtra("item_position",0);
        if(BuildConfig.DEBUG) Log.d("HarjutusteKalendrAct", "onActivityResult. requestCode:" + requestCode + " Pos:" + itemposition);

        if (requestCode == getResources().getInteger(R.integer.KALENDER_ACTIVITY_INTENT_HARJUTUS_MUUDA)) {
            if (resultCode == getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_MUUDA)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi HarjutusMuudaActivity, muuda. Pos:" + itemposition);
                mMainAdapter.notifyItemChanged(itemposition);            }
            if (resultCode == getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_KUSTUTATUD)) {
                itemposition = data.getIntExtra("item_position",0);
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi HarjutusMuudaActivity, kustutatud. Pos:" + itemposition);
                pPaevadeList.clear();
                Tooriistad.LooKuupaevad(getApplicationContext(), 60, pPaevadeList);
                mMainAdapter.notifyDataSetChanged();
            }
        }
    }
}

