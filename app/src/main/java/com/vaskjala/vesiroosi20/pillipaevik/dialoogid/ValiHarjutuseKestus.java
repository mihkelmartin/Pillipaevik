package com.vaskjala.vesiroosi20.pillipaevik.dialoogid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import com.vaskjala.vesiroosi20.pillipaevik.R;

/**
 * Created by mihkel on 19.05.2016.
 */
public class ValiHarjutuseKestus extends DialogFragment {

    // Use this instance of the interface to deliver action events
    LihtsaKusimuseKuulaja mListener;
    private NumberPicker kestus;

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
        // Use the current date as the default date in the picker
        Log.d("Valikuupaev", "Loon pikkusevaliku dialoogi");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialoog_kestus, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Bundle args = getArguments();
                        args.putInt("kestus", kestus.getValue());
                        mListener.kuiJahVastus(ValiHarjutuseKestus.this);
                    }
                })
                .setNegativeButton("Loobu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.kuiEiVastus(ValiHarjutuseKestus.this);
                    }
                });
        kestus = (NumberPicker) v.findViewById(R.id.kestusminutites);
        kestus.setMinValue(0);
        kestus.setMaxValue(getArguments().getInt("maksimum"));
        kestus.setValue(getArguments().getInt("kestus"));
        return builder.create();
    }

}
