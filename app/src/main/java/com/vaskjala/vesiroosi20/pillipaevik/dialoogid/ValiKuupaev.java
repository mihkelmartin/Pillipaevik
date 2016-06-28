package com.vaskjala.vesiroosi20.pillipaevik.dialoogid;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mihkel on 19.05.2016.
 */
public class ValiKuupaev extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

    private final Calendar c = Calendar.getInstance();
    private AjaMuutuseTeavitus KuupaevaOmanik;


    public void onAttach(Activity a) {
        super.onAttach(a);
        KuupaevaOmanik = (AjaMuutuseTeavitus) a;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        if(BuildConfig.DEBUG) Log.d("Valikuupaev", "Loon kuupäeva valiku dialoogi");

        c.setTime(Tooriistad.KuupaevKellaAegStringist(getArguments().getString("datetime")));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        c.set(year,month,day);
        KuupaevaOmanik.AegMuudetud(c.getTime(), getArguments().getBoolean("muudaalgust"));
        if(BuildConfig.DEBUG) Log.d("Valikuupaev", year + "-" + month + "-"+ day + " " + c.getTime());
    }
}
