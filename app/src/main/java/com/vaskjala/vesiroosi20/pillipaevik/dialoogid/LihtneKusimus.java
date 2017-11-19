package com.vaskjala.vesiroosi20.pillipaevik.dialoogid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;


/**
 * Created by mihkel on 24.05.2016.
 */
public class LihtneKusimus extends DialogFragment {


    private LihtsaKusimuseKuulaja mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if(BuildConfig.DEBUG) Log.d("LihtneKusimus", "Ühendati Activity");
            mListener = (LihtsaKusimuseKuulaja) activity;
        } catch (ClassCastException e) {
            if(BuildConfig.DEBUG) Log.d("LihtneKusimus","onAttach. Activityl ei ole liides defineeritud");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int fragmendiid = getArguments().getInt("fragmendiID",0);
        if(fragmendiid != 0);
        {
            try {
                if (BuildConfig.DEBUG) Log.d("LihtneKusimus", "Ühendati Fragment");
                mListener = (LihtsaKusimuseKuulaja) getFragmentManager().findFragmentById(fragmendiid);
            } catch (ClassCastException e) {
                throw new ClassCastException("Fragmendil ei ole LihtsaKusimuseKuulaja liides defineeritud");
            }
        }

        if(mListener == null)
            throw new ClassCastException("LihtneKusimus. mListener == null");

        String pealkiri = getArguments().getString("pealkiri","");
        String kysimus = getArguments().getString("kysimus","");
        String jahvastus = getArguments().getString("jahvastus","");
        String eivastus = getArguments().getString("eivastus","");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(pealkiri)
                .setMessage(kysimus)
                .setPositiveButton(jahvastus, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.kuiJahVastus(LihtneKusimus.this);
                    }
                })
                .setNegativeButton(eivastus, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.kuiEiVastus(LihtneKusimus.this);
                    }
                });
        return builder.create();
    }
}