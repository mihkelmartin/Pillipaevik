package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
    private static final SimpleDateFormat sdfkuupaevkellaaegBackup = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

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

    public static String KujundaHarjutusteMinutidTabloo (int minutid ) {
        long hours = 0, minutes = minutid;

        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= hours * 60;
        }
        return formatDigits(hours) + ":" + formatDigits(minutes);
    }

    // Varukoopia
    public static void exportDB(Context context){

        File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + PilliPaevikDatabase.DATABASE_NAME + File.separator );

        boolean success = true;
        if (!sd.exists()) {
            Log.d("exportDB", "Soovitakse luua:" + sd.getAbsolutePath());
            success = sd.mkdir();
        }
        if (success) {

            File data = Environment.getDataDirectory();
            FileChannel source=null;
            FileChannel destination=null;
            String currentDBPath = "/data/"+ context.getPackageName() +"/databases/"+PilliPaevikDatabase.DATABASE_NAME;
            String backupDBPath = PilliPaevikDatabase.DATABASE_NAME + KujundaKuupaevKellaaegBackup(new Date());
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);
            Log.d("exportDB", "Originaal:" + currentDB.getAbsolutePath());
            Log.d("exportDB", "Koopia:" + backupDB.getAbsolutePath());

            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("exportDB", "Download kataloogi loomine ei õnnestunud.");
        }
    }

    // Taastamine
    private static void importDB(Context context){
        File sd = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + PilliPaevikDatabase.DATABASE_NAME+
                File.separator );
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String backupDBPath = "/data/"+ context.getPackageName() +"/databases/"+PilliPaevikDatabase.DATABASE_NAME;
        String currentDBPath = PilliPaevikDatabase.DATABASE_NAME + "Taasta";
        File currentDB = new File(sd, currentDBPath);
        File backupDB = new File(data, backupDBPath);
        Log.d("importDB", "Varukoopia:" + currentDB.getAbsolutePath());
        Log.d("importDB", "Taastatud baas:" + backupDB.getAbsolutePath());
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            currentDB.delete();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

