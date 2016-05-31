package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mihkel on 27.05.2016.
 */
public final class Tooriistad {

    private static final Calendar c = Calendar.getInstance();
    private static final SimpleDateFormat sdfkuupaev = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat sdfkellaaeg = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat sdfkuupaevkellaaeg = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat sdfkuupaevkellaaegBackup = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    public static Date HetkeKuupaevNullitudSekunditega(){
        c.setTime( new Date() );
        c.set( Calendar.SECOND, 0 );
        c.set( Calendar.MILLISECOND, 0 );
        return c.getTime();
    }

    public static void NaitaHoiatust(Activity activity, String pealkiri, String hoiatus) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(pealkiri);
        alertDialog.setMessage(hoiatus);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static String KujundaKuupaev(Date kuupaev){
        return sdfkuupaev.format(kuupaev);
    }
    public static String KujundaKellaaeg(Date kuupaev){
        return sdfkellaaeg.format(kuupaev);
    }
    public static String KujundaKuupaevKellaaeg(Date kuupaev){
        return sdfkuupaevkellaaeg.format(kuupaev);
    }
    public static String KujundaKuupaevKellaaegBackup(Date kuupaev){
        return sdfkuupaevkellaaegBackup.format(kuupaev);
    }

    public static String formatElapsedTime(long now) {
        long hours = 0, minutes = 0, seconds = 0, tenths = 0;
        StringBuilder sb = new StringBuilder();
        if (now < 1000) {
            tenths = now / 100;
        } else if (now < 60000) {
            seconds = now / 1000;
            now -= seconds * 1000;
            tenths = (now / 100);
        } else {
            hours = now / 3600000;
            now -= hours * 3600000;
            minutes = now / 60000;
            now -= minutes * 60000;
            seconds = now / 1000;
            now -= seconds * 1000;
            tenths = (now / 100);
        }
         sb.append(formatDigits(hours)).append(":")
                .append(formatDigits(minutes)).append(":")
                .append(formatDigits(seconds));
        return sb.toString();
    }
    private static String formatDigits(long num) {
        return (num < 10) ? "0" + num : new Long(num).toString();
    }

    public static String KujundaHarjutusteMinutid (int minutid ) {
        long hours = 0, minutes = minutid;

        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= hours * 60;
        }
        String strtunnid = (hours == 0) ? "" : (hours == 1) ? hours + " tund" : hours + " tundi";
        String strminutid = (minutes == 0) ? "" : (minutes == 1) ? minutes + " minut" : minutes + " minutit";
        String kokku = strtunnid + " " + strminutid;
        return kokku;
    }


}

