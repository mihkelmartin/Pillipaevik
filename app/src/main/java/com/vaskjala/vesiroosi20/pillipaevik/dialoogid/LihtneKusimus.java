package com.vaskjala.vesiroosi20.pillipaevik.dialoogid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.vaskjala.vesiroosi20.pillipaevik.R;

/**
 * Created by mihkel on 24.05.2016.
 */
public class LihtneKusimus extends DialogFragment {


    private LihtsaKusimuseKuulaja mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LihtsaKusimuseKuulaja) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " LihtsaKusimuseKuulaja peab olema implementeeritud");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

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