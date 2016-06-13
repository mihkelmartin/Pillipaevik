package com.vaskjala.vesiroosi20.pillipaevik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.*;


/**
 * Created by mihkel on 12.06.2016.
 */
public class HarjutusteKalendriLeht extends Fragment {

    public HarjutusteKalendriLeht(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        List<PaevaKirje> pPaevadeList = LooKuupaevad(60);
        View rootView = inflater.inflate(
                R.layout.harjutuste_kalendri_lehekulg, container, false);

        HarjutusKalenderPaevadRecyclerViewAdapter mMainAdapter =
                new HarjutusKalenderPaevadRecyclerViewAdapter(pPaevadeList);
        ((RecyclerView)rootView).setAdapter(mMainAdapter);
        Bundle args = getArguments();
        return rootView;
    }

    private List<PaevaKirje>  LooKuupaevad(int paevi){
        List<PaevaKirje> mPL = new ArrayList<PaevaKirje>();
        Calendar c = Calendar.getInstance();
        Calendar calgus = Calendar.getInstance();
        calgus.add(Calendar.DAY_OF_MONTH, -1 * paevi);
        PilliPaevikDatabase pilliPaevikDatabase = new PilliPaevikDatabase(getContext());
        HashMap<Long, PaevaKirje> mHM = pilliPaevikDatabase.HarjutusteStatistikaPerioodisPaevaKaupa(calgus.getTime(),c.getTime());

        c.setTime(Tooriistad.HetkeKuupaevNullitudKellaAjaga());
        for(int i = 0; i< paevi ; i++) {
            c.add(Calendar.DAY_OF_MONTH, -1);
            PaevaKirje mPK = new PaevaKirje(c.getTime(), 0, 0);
            PaevaKirje mAndmebaasist = mHM.get(c.getTimeInMillis());
            if(mAndmebaasist != null) {
                mPK.kordadearv = mAndmebaasist.kordadearv;
                mPK.pikkussekundites = mAndmebaasist.pikkussekundites;
            }
            mPL.add(mPK);
        }
        return  mPL;

    }
}
