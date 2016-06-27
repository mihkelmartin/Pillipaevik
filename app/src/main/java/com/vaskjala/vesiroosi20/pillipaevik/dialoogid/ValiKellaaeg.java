package com.vaskjala.vesiroosi20.pillipaevik.dialoogid;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mihkel on 19.05.2016.
 */
public class ValiKellaaeg extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

    private final Calendar c = Calendar.getInstance();
    private AjaMuutuseTeavitus KuupaevaOmanik;

    public void onAttach(Activity a) {
        super.onAttach(a);
        KuupaevaOmanik = (AjaMuutuseTeavitus) a;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        if(BuildConfig.DEBUG) Log.d("Valikellaaeg", "Loon kellaaja dialoogi");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try{
         c.setTime(sdf.parse(getArguments().getString("datetime")));
        } catch (ParseException pe) {
            System.out.println(pe);
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        if(BuildConfig.DEBUG) Log.d("ValikKellaaeg",hour + ":" + minute + " 24H" + DateFormat.is24HourFormat(getActivity()));

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    public void onTimeSet(TimePicker view, int hour, int minute) {
        // Kellaaega sätitakse minuti täpsusega. Nulli sekundid.
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hour, minute, 0);
        KuupaevaOmanik.AegMuudetud(c.getTime(), getArguments().getBoolean("muudaalgust"));
        if(BuildConfig.DEBUG) Log.d("Valikuupaev", hour + ":" + minute + " " + c.getTime());
    }
}
