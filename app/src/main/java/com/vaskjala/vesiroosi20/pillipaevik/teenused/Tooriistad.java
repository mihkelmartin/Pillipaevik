package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mihkel on 27.05.2016.
 */
public final class Tooriistad {

    public static final int GOOGLE_DRIVE_KONTO_VALIMINE = 1000;
    public static final int GOOGLE_DRIVE_REST_KONTO_VALIMINE = 1001;
    public static final int GOOGLE_DRIVE_REST_UHENDUSE_LUBA = 1004;

    private static final Calendar c = Calendar.getInstance();
    private static final SimpleDateFormat sdfkuupaev = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat sdfkellaaeg = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat sdfkuupaevkellaaeg = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat sdfkuupaevkellaaegBackup = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static final SimpleDateFormat sdfkuupaevkellaaegFailiNimi = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    private static final SimpleDateFormat sdfkuupaevSonaline = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
    private static final SimpleDateFormat sdfkuupaevSonalineLuhike = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
    private static final SimpleDateFormat sdfkuuJaaastaSonaline = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());


    public static Date KuupaevStringist(String kuupaev){

        Date retVal = null;
        try {
            retVal = sdfkuupaev.parse(kuupaev);
        } catch (ParseException pe) {
            if(BuildConfig.DEBUG) Log.d("Tooriistad", "KuupaevStringist. Ei suuda kujundada stringist kuupäeva:" + sdfkuupaev);
        }
        return retVal;
    }

    public static Date KuupaevKellaAegStringist(String kuupaev){

        Date retVal = null;
        try {
            retVal = sdfkuupaevkellaaeg.parse(kuupaev);
        } catch (ParseException pe) {
            if(BuildConfig.DEBUG) Log.d("Tooriistad", "KuupaevKellaAegStringist. Ei suuda kujundada stringist kuupäeva:" + sdfkuupaev);
        }
        return retVal;
    }

    public static Date KuupaevKuuJaAastaSonalineStringist(String kuupaev){

        Date retVal = null;
        try {
            retVal = sdfkuuJaaastaSonaline.parse(kuupaev);
        } catch (ParseException pe) {
            if(BuildConfig.DEBUG) Log.d("Tooriistad", "KuupaevKuuJaAastaSonalineStringist. Ei suuda kujundada stringist kuupäeva:" + sdfkuuJaaastaSonaline);
        }
        return retVal;
    }

    public static String KujundaKuupaev(Date kuupaev){
        return sdfkuupaev.format(kuupaev);
    }
    public static String KujundaKuupaevSonaline(Date kuupaev){
        return sdfkuupaevSonaline.format(kuupaev);
    }
    public static String KujundaKuupaevSonalineLuhike(Date kuupaev){
        return sdfkuupaevSonalineLuhike.format(kuupaev);
    }

    public static String KujundaKuuJaAastaSonaline(Date kuupaev){
        return sdfkuuJaaastaSonaline.format(kuupaev);
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
    public static String KujundaKuupaevKellaaegFailiNimi(Date kuupaev){
        return sdfkuupaevkellaaegFailiNimi.format(kuupaev);
    }

    // Eriliste kuupäevade loomine
    // yyyy-mm-dd hh:mm:00.000000
    public static Date HetkeKuupaevNullitudSekunditega(){
        c.setTime( new Date() );
        c.set( Calendar.SECOND, 0 );
        c.set( Calendar.MILLISECOND, 0 );
        return c.getTime();
    }
    // yyyy-mm-dd 00:00:00.000000
    public static Date HetkeKuupaevNullitudKellaAjaga(){
        c.setTime( new Date() );
        c.set( Calendar.HOUR_OF_DAY, 0 );
        c.set( Calendar.MINUTE, 0 );
        c.set( Calendar.SECOND, 0 );
        c.set( Calendar.MILLISECOND, 0 );
        return c.getTime();
    }

    public static Date MoodustaNädalaAlgusKuupaev(Date kuupaev){
        c.setTime( kuupaev );
        int paev = c.get(Calendar.DAY_OF_WEEK);
        paev = (paev == 1) ? 7 : paev - 1;
        c.add(Calendar.DAY_OF_MONTH, (-1 * paev) + 1);
        return c.getTime();
    }
    public static Date MoodustaNädalaLopuKuupaev(Date kuupaev){
        c.setTime( kuupaev );
        int paev = c.get(Calendar.DAY_OF_WEEK);
        paev = (paev == 1) ? 7 : paev - 1;
        c.add(Calendar.DAY_OF_MONTH, 7 - paev);
        return c.getTime();
    }
    public static Date MoodustaKuuAlgusKuupaev(Date kuupaev){
        c.setTime( kuupaev );
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }
    public static Date MoodustaKuuLopuKuupaev(Date kuupaev){
        c.setTime( kuupaev );
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DATE));
        return c.getTime();
    }

    // Kuupäevaarvutused
    public static int KaheKuupaevaVahePaevades(Date esimene, Date teine){
        return (int)((teine.getTime() - esimene.getTime()) / 1000 / 60 / 60 / 24);
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
    public static String formatDigits(long num) {
        return (num < 10) ? "0" + num : Long.valueOf(num).toString();
    }

    public static String KujundaHarjutusteMinutid (int minutid ) {
        long hours = 0, minutes = minutid;

        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= hours * 60;
        }
        String strtunnid = (hours == 0) ? "" : (hours == 1) ? hours + " tund" : hours + " tundi";
        String strminutid = (minutes == 0) ? "" : (minutes == 1) ? minutes + " minut" : minutes + " minutit";
        return strtunnid + " " + strminutid;
    }
    public static String KujundaHarjutusteMinutidTabloo (int minutid ) {
        long hours = 0, minutes = minutid;

        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= hours * 60;
        }
        return formatDigits(hours) + ":" + formatDigits(minutes);
    }

    // Aja arvutused
    public static int ArvutaMinutidUmardaUles(int sekundid) {
        return (int) Math.ceil((double) sekundid / 60.0);
    }

    // Helifailide salvestamine
    public static boolean KasLubadaSalvestamine(Context context){
        boolean retVal;
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.seadete_fail), MODE_PRIVATE);
        retVal = sharedPref.getBoolean("kaslubadamikrofonigasalvestamine", true);
        return  retVal;
    }

    public static boolean kasKasutadaGoogleDrive(Context context){
        boolean retVal;
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.seadete_fail), MODE_PRIVATE);
        retVal = sharedPref.getBoolean("kaskasutadagoogledrive", true);
        return  retVal;
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

    public static List<String> LooAruandeKuud(int kuudearv){
        List<String> retVal = new ArrayList<String>();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        for(int i=0; i < kuudearv; i++){
            retVal.add(KujundaKuuJaAastaSonaline(c.getTime()));
            c.add(Calendar.MONTH, -1);
            if(BuildConfig.DEBUG) Log.d("LooAruandeKuud", KujundaKuuJaAastaSonaline(c.getTime()));
        }
        return retVal;
    }

    // Varukoopia
    public static void exportDB(Context context){

        File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + PilliPaevikDatabase.DATABASE_NAME + File.separator );

        boolean success = true;
        if (!sd.exists()) {
            if(BuildConfig.DEBUG) Log.d("exportDB", "Soovitakse luua:" + sd.getAbsolutePath());
            success = sd.mkdir();
        }
        if (success) {

            File data = Environment.getDataDirectory();
            FileChannel source;
            FileChannel destination;
            String currentDBPath = "/data/"+ context.getPackageName() +"/databases/"+PilliPaevikDatabase.DATABASE_NAME;
            String backupDBPath = PilliPaevikDatabase.DATABASE_NAME + KujundaKuupaevKellaaegBackup(new Date());
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);
            if(BuildConfig.DEBUG) Log.d("exportDB", "Originaal:" + currentDB.getAbsolutePath());
            if(BuildConfig.DEBUG) Log.d("exportDB", "Koopia:" + backupDB.getAbsolutePath());

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
            if(BuildConfig.DEBUG) Log.d("exportDB", "Download kataloogi loomine ei õnnestunud.");
        }
    }
    // Taastamine
    public static void importDB(Context context){
        File sd = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + PilliPaevikDatabase.DATABASE_NAME+
                File.separator );
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String backupDBPath = "/data/"+ context.getPackageName() +"/databases/"+PilliPaevikDatabase.DATABASE_NAME;
        String currentDBPath = PilliPaevikDatabase.DATABASE_NAME + "Taasta";
        File currentDB = new File(sd, currentDBPath);
        File backupDB = new File(data, backupDBPath);
        if(BuildConfig.DEBUG) Log.d("importDB", "Varukoopia:" + currentDB.getAbsolutePath());
        if(BuildConfig.DEBUG) Log.d("importDB", "Taastatud baas:" + backupDB.getAbsolutePath());
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


    public static boolean KustutaKohalikFail(File dir, String failinimi){
        boolean retVal;
        File file = new File(dir, failinimi);
        if(file.delete()) {
            if (BuildConfig.DEBUG) Log.d("Tooriistad", "Kohalik fail kustutatud :" + dir.getPath() + "/" + failinimi);
            retVal = true;
        }
        else {
            if (BuildConfig.DEBUG) Log.e("Tooriistad", "Kohaliku faili kustutamisel viga !");
            retVal = false;
        }
        return retVal;
    }

}

