package com.vaskjala.vesiroosi20.pillipaevik.aruanded;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.dialoogid.LihtsaKusimuseKuulaja;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.List;

/**
 * Created by mihkel on 27.06.2016.
 */
public class ValiAruandeKuu extends DialogFragment {

    private LihtsaKusimuseKuulaja mListener;

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
