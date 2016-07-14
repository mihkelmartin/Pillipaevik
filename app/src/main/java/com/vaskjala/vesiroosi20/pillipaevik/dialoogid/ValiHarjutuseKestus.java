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
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.R;

/**
 * Created by mihkel on 19.05.2016.
 */
public class ValiHarjutuseKestus extends DialogFragment {

    private LihtsaKusimuseKuulaja mListener;
    private NumberPicker kestus;

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

        if(BuildConfig.DEBUG) Log.d("ValiHarjutuseKestus", "Loon pikkusevaliku dialoogi");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialoog_kestus, null);

        builder.setView(v)
                .setTitle(R.string.muuda_harjutuse_kestust)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Bundle args = getArguments();
                        args.putInt("kestus", kestus.getValue());
                        mListener.kuiJahVastus(ValiHarjutuseKestus.this);
                    }
                })
                .setNegativeButton(R.string.loobu, new DialogInterface.OnClickListener() {
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
