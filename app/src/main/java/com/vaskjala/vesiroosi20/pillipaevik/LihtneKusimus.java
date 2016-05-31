package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by mihkel on 24.05.2016.
 */
public class LihtneKusimus extends DialogFragment {


    // Use this instance of the interface to deliver action events
    LihtsaKusimuseKuulaja mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (LihtsaKusimuseKuulaja) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String kysimus = getArguments().getString("kysimus","");
        String jahvastus = getArguments().getString("jahvastus","");
        String eivastus = getArguments().getString("eivastus","");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(kysimus)
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
        // Create the AlertDialog object and return it
        return builder.create();
    }
}