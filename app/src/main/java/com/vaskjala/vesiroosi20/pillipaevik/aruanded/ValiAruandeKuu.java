package com.vaskjala.vesiroosi20.pillipaevik.aruanded;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.List;

/**
 * Created by mihkel on 27.06.2016.
 */
public class ValiAruandeKuu extends DialogFragment {

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

        List<String> mKuud  = Tooriistad.LooAruandeKuud(getResources().getInteger(R.integer.kuudearv));
        final CharSequence[] cs = mKuud.toArray(new CharSequence[mKuud.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.vali_aruande_kuu)
                .setItems(cs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle args = getArguments();
                        args.putString("kuujaaasta", (String) cs[which]);
                        mListener.kuiJahVastus(ValiAruandeKuu.this);                    }
                })
                .setNegativeButton(R.string.loobu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.kuiEiVastus(ValiAruandeKuu.this);
                    }
                });
        return builder.create();
    }
}
