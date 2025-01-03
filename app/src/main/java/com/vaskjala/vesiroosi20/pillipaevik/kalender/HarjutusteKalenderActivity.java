package com.vaskjala.vesiroosi20.pillipaevik.kalender;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.*;

import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;



/**
 * Created by mihkel on 11.06.2016.
 */
public class HarjutusteKalenderActivity extends AppCompatActivity implements HarjutusteKalenderFragmendiKuulaja, HarjutusFragmendiKuulaja {

    private boolean bMitmeFragmendiga = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutuste_kalender);
        if (findViewById(R.id.harjutus_hoidja) != null) {
            if (BuildConfig.DEBUG) Log.d(getLocalClassName(), "onCreate. Mitme fragmendiga vaade");
            bMitmeFragmendiga = true;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.kalender_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int itemposition = 0;
        if(data != null)
            itemposition = data.getIntExtra("item_position",0);

        if(BuildConfig.DEBUG) Log.d("HarjutusteKalendrAct", "onActivityResult. requestCode:" + requestCode + " Pos:" + itemposition);

        if (requestCode == getResources().getInteger(R.integer.KALENDER_ACTIVITY_INTENT_HARJUTUS_MUUDA)) {
            HarjutusteKalenderFragment harjutusteKalenderFragment= (HarjutusteKalenderFragment) getFragmentManager().findFragmentById(R.id.harjutustekalenderfragment);
            if (resultCode == getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_MUUDA)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi HarjutusMuudaActivity, muuda. Pos:" + itemposition);
                harjutusteKalenderFragment.mMainAdapter.notifyItemChanged(itemposition);            }
            if (resultCode == getResources().getInteger(R.integer.HARJUTUS_ACTIVITY_RETURN_KUSTUTATUD)) {
                if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi HarjutusMuudaActivity, kustutatud. Pos:" + itemposition);
                harjutusteKalenderFragment.pPaevadeList.clear();
                Tooriistad.LooKuupaevad(getApplicationContext(), 60, harjutusteKalenderFragment.pPaevadeList);
                harjutusteKalenderFragment.mMainAdapter.notifyDataSetChanged();
                if(data != null) {
                    int kustutamisealge = data.getIntExtra("kustutamisealge", 0);
                    if (kustutamisealge == Tooriistad.TUHIHARJUTUS_KUSTUTA)
                        Tooriistad.KuvaAutomaatseKustutamiseTeade(this);
                }
            }
        }
    }

    @Override
    public void HarjutusLisatud(int teosid, int harjutusid) {

    }

    @Override
    public void HarjutusValitud(int teosid, int harjutusid, int itemposition) {

        if(bMitmeFragmendiga){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            SuleHarjutusFragment(ft);
            PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(getApplicationContext());
            boolean bHarjutusOlemas = mPPManager.getHarjutus(teosid, harjutusid) != null;
            if(bHarjutusOlemas) {
                Fragment harjutusMuudaFragment = new HarjutusMuudaFragment();
                Bundle args = new Bundle();
                args.putInt("teos_id", teosid);
                args.putInt("harjutus_id", harjutusid);
                args.putInt("item_position", itemposition);
                harjutusMuudaFragment.setArguments(args);
                ft.replace(R.id.harjutus_hoidja, harjutusMuudaFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            Intent intent = new Intent(this, HarjutusMuudaActivity.class);
            intent.putExtra("teos_id", teosid);
            intent.putExtra("harjutus_id", harjutusid);
            intent.putExtra("item_position", itemposition);
            if (BuildConfig.DEBUG) Log.d("HarjutusteKalenderAct", "Avan olemasolevat harjutust. Teosid : " + teosid +
                    " Harjutus:" + harjutusid);
            startActivityForResult(intent, getResources().getInteger(R.integer.KALENDER_ACTIVITY_INTENT_HARJUTUS_MUUDA));
        }
    }

    @Override
    public void PaevValitud() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SuleHarjutusFragment(ft);
        ft.commit();
    }

    @Override
    public void HarjutusKustutatud(int teosid, int harjutusid, int itemposition, int kustutamisealge) {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi HarjutusMuudaFragmendilt, kustutatud, Pos:" + itemposition);
        HarjutusteKalenderFragment harjutusteKalenderFragment= (HarjutusteKalenderFragment) getFragmentManager().findFragmentById(R.id.harjutustekalenderfragment);

        HarjutuskordKirje harjutuskordKirje = ((HarjutuskordKirje)harjutusteKalenderFragment.pPaevadeList.get(itemposition));
        harjutuskordKirje.vanem.Harjutused.remove(harjutuskordKirje);
        harjutuskordKirje.vanem.kordadearv = harjutuskordKirje.vanem.kordadearv - 1;
        harjutuskordKirje.vanem.pikkussekundites = harjutuskordKirje.vanem.pikkussekundites - harjutuskordKirje.harjutusKord.getPikkussekundites();
        harjutusteKalenderFragment.mMainAdapter.notifyItemChanged(harjutusteKalenderFragment.pPaevadeList.indexOf(harjutuskordKirje.vanem));

        harjutusteKalenderFragment.pPaevadeList.remove(itemposition);
        harjutusteKalenderFragment.mMainAdapter.notifyItemRemoved(itemposition);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if(harjutusfragment != null) {
            ft.remove(harjutusfragment);
        }
        ft.commit();
        if(kustutamisealge == Tooriistad.TUHIHARJUTUS_KUSTUTA)
            Tooriistad.KuvaAutomaatseKustutamiseTeade(this);
    }

    @Override
    public void HarjutusMuudetud(int teosid, int harjutusid, int position) {
        if(BuildConfig.DEBUG) Log.d(getLocalClassName(), "Tagasi HarjutusMuudaFragmendilt, muudetud, Pos:" + position);
        HarjutusteKalenderFragment harjutusteKalenderFragment= (HarjutusteKalenderFragment) getFragmentManager().findFragmentById(R.id.harjutustekalenderfragment);
        harjutusteKalenderFragment.mMainAdapter.notifyItemChanged(position);
    }

    @Override
    public void SeaHarjutusid(int harjutuseid) {

    }

    private void SuleHarjutusFragment(FragmentTransaction ft){
        Fragment harjutusfragment = getFragmentManager().findFragmentById(R.id.harjutus_hoidja);
        if (harjutusfragment != null) {
            ((HarjutusFragmendiKutsuja) harjutusfragment).SuleHarjutus();
            ft.remove(harjutusfragment);
        }
    }
}

