package com.vaskjala.vesiroosi20.pillipaevik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

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

        List<PaevaKirje> pPaevadeList = Tooriistad.LooKuupaevad(getActivity().getApplicationContext(), 60);

        View rootView = inflater.inflate(
                R.layout.harjutuste_kalendri_lehekulg, container, false);

        HarjutusKalenderPaevadRecyclerViewAdapter mMainAdapter =
                new HarjutusKalenderPaevadRecyclerViewAdapter(pPaevadeList);
        ((RecyclerView)rootView).setAdapter(mMainAdapter);
        return rootView;
    }


}
