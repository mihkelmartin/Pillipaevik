package com.vaskjala.vesiroosi20.pillipaevik.kalender;

import android.app.Activity;
import android.app.Fragment;;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mihkel on 11.06.2016.
 */
public class HarjutusteKalenderFragment extends Fragment {

    protected HarjutusKalenderPaevadRecyclerViewAdapter mMainAdapter = null;
    protected List<KalendriKirje> pPaevadeList = new ArrayList<KalendriKirje>();
    private LaeKalendriAndmed laeKalendriAndmed = null;

    private HarjutusteKalenderFragmendiKuulaja harjutusteKalenderFragmendiKuulaja;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            harjutusteKalenderFragmendiKuulaja = (HarjutusteKalenderFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama HarjutusFragmendiKuulaja");
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            harjutusteKalenderFragmendiKuulaja = (HarjutusteKalenderFragmendiKuulaja) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " peab teostama HarjutusFragmendiKuulaja");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("HarjutusteKalenderFrag", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_harjutuste_kalender, container, false);

        RecyclerView mKalendriTabel = (RecyclerView) view.findViewById(R.id.kalendri_tabel);
        mMainAdapter = new HarjutusKalenderPaevadRecyclerViewAdapter(pPaevadeList, harjutusteKalenderFragmendiKuulaja);
        mKalendriTabel.setAdapter(mMainAdapter);

        laeKalendriAndmed = new LaeKalendriAndmed();
        laeKalendriAndmed.execute();
        return view;
    }

    @Override
    public void onStop() {
        if(laeKalendriAndmed != null)
            laeKalendriAndmed.cancel(false);
        super.onStop();
    }

    private class LaeKalendriAndmed extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void ... params) {
            if(!isCancelled())
                Tooriistad.LooKuupaevad(getActivity().getApplicationContext(), 60, pPaevadeList);
            return null;
        }

        protected void onPostExecute(Void result) {
            mMainAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }
}

