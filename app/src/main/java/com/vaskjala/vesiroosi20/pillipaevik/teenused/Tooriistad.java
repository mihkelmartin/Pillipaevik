package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.kalender.KalendriKirje;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.kalender.PaevaKirje;

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
    public static final int GOOGLE_PLAY_TEENUSTE_VEAAKEN = 1010;

    public static final int ANDMEBAASI_VARUKOOPIATE_MAKSIMUM_ARV = 15;

    public static final long MAKSIMAALNE_HELIFAILIPIKKUS_MILLISEKUNDITES = 30 * 60 * 1000;

    public static final int KORRALDA_LOAD = 1;

    public static final int KASUTAJA_KUSTUTAS = 1;
    public static final int TUHIHARJUTUS_KUSTUTA = 2;

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

    public static Date MoodustaNihkegaKuupaev(int minutid){
        c.setTime( HetkeKuupaevNullitudSekunditega() );
        c.add(Calendar.MINUTE, (-1 * (minutid)) );
        return c.getTime();

    }

    public static String KujundaAeg(long now) {
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
         sb.append(KujundaNumbrid(hours)).append(":")
                .append(KujundaNumbrid(minutes)).append(":")
                .append(KujundaNumbrid(seconds));
        return sb.toString();
    }
    public static String KujundaNumbrid(long num) {
        return (num < 10) ? "0" + num : Long.valueOf(num).toString();
    }

    public static String KujundaHarjutusteMinutid (Context context, int minutid ) {
        long hours = 0, minutes = minutid;

        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= hours * 60;
        }
        String tund = context.getString(R.string.tund);
        String tundi = context.getString(R.string.tundi);
        String minut = context.getString(R.string.minut);
        String minutit = context.getString(R.string.minutit);

        String strtunnid = (hours == 0) ? "" : (hours == 1) ? hours + " " + tund : hours + " " + tundi;
        String strminutid = (minutes == 0) ? "" : (minutes == 1) ? minutes + " " + minut : minutes + " " + minutit;
        return strtunnid + " " + strminutid;
    }
    public static String KujundaHarjutusteMinutidTabloo (int minutid ) {
        long hours = 0, minutes = minutid;

        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= hours * 60;
        }
        return KujundaNumbrid(hours) + ":" + KujundaNumbrid(minutes);
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

    public static boolean kasNimedEpostOlemas(Context context){
        boolean retVal;
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.seadete_fail), MODE_PRIVATE);
        if(sharedPref.getString("minueesnimi","").isEmpty() ||
                sharedPref.getString("minuperenimi","").isEmpty() ||
                sharedPref.getString("minuinstrument","").isEmpty()){
            retVal = false;
        } else {
            retVal = true;
        }
        return  retVal;    }

    public static void NaitaHoiatust(Activity activity, String pealkiri, String hoiatus) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(pealkiri);
        alertDialog.setMessage(hoiatus);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void KuvaAutomaatseKustutamiseTeade(Activity activity){
        Snackbar.make(activity.findViewById(android.R.id.content),
                R.string.snackbar_harjutuse_automaatne_kustutamine, Snackbar.LENGTH_LONG).show();
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

    public static void LooKuupaevad(Context context, int paevi, List<KalendriKirje> mPL){

        Calendar c = Calendar.getInstance();
        Calendar calgus = Calendar.getInstance();
        calgus.add(Calendar.DAY_OF_MONTH, -1 * paevi);

        PilliPaevikDatabase pilliPaevikDatabase = new PilliPaevikDatabase(context);
        HashMap<Long, KalendriKirje> mHM = pilliPaevikDatabase.HarjutusteStatistikaPerioodisPaevaKaupa(calgus.getTime(),c.getTime());

        c.setTime(Tooriistad.HetkeKuupaevNullitudKellaAjaga());
        c.add(Calendar.DAY_OF_MONTH, 1);

        for(int i = 0; i< paevi ; i++) {
            c.add(Calendar.DAY_OF_MONTH, -1);
            KalendriKirje mPK = mHM.get(c.getTimeInMillis());
            if(mPK == null) {
                mPK = new PaevaKirje(KalendriKirje.Tyyp.PAEV, "", c.getTime(), 0, 0);
            }
            mPL.add(mPK);
        }
    }

    public static void KorraldaLoad(Activity activity){

        List<String> PuuduvadLoad = new ArrayList<String>();

        if (KasLubadaSalvestamine(activity.getApplicationContext())){

            if(ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                PuuduvadLoad.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                PuuduvadLoad.add(Manifest.permission.RECORD_AUDIO);
        }

        if (kasKasutadaGoogleDrive(activity.getApplicationContext()) &&
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.GET_ACCOUNTS)
                        != PackageManager.PERMISSION_GRANTED) {
            PuuduvadLoad.add(Manifest.permission.GET_ACCOUNTS);
        }

        if(!PuuduvadLoad.isEmpty())
            ActivityCompat.requestPermissions(activity, PuuduvadLoad.toArray(new String[0]), KORRALDA_LOAD);
    }

    public static void SeadistaSalvestamiseOlek(Context context){

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.seadete_fail), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("kaslubadamikrofonigasalvestamine", false);
            editor.commit();

        }
    }

    public static void SeadistaGoogleDriveOlek(Context context){

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.seadete_fail), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("kaskasutadagoogledrive", false);
            editor.commit();
        }
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
                synchronized (PilliPaevikDatabase.sPilliPaevikuLukk) {
                    if(BuildConfig.DEBUG) Log.d("exportDB", "Sünkroniseeritud osas tegelik kopeerimine" );
                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            // Kustuta liigsed varukoopiad
            List<String> pFailid = Arrays.asList(sd.list());
            int varukoopiatearv = pFailid.size();
            if(varukoopiatearv > ANDMEBAASI_VARUKOOPIATE_MAKSIMUM_ARV ) {
                Collections.sort(pFailid, Collections.<String>reverseOrder());
                for (int i = 0; i <= varukoopiatearv - ANDMEBAASI_VARUKOOPIATE_MAKSIMUM_ARV - 1 ;i++) {
                    Tooriistad.KustutaKohalikFail(sd, pFailid.get(ANDMEBAASI_VARUKOOPIATE_MAKSIMUM_ARV + i));
                }
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
            if(BuildConfig.DEBUG) Log.e("importDB", "Faili ei leitud. Otsiti:" + backupDB.getAbsolutePath()+ "/" + currentDBPath);
        }
    }

    public static boolean KustutaKohalikFail(File dir, String failinimi){
        boolean retVal;

        if(failinimi != null) {
            File file = new File(dir, failinimi);
            if (file.delete()) {
                if (BuildConfig.DEBUG)
                    Log.d("Tooriistad", "Kohalik fail kustutatud :" + dir.getPath() + "/" + failinimi);
                retVal = true;
            } else {
                if (BuildConfig.DEBUG) Log.e("Tooriistad", "Kohaliku faili kustutamisel viga !");
                retVal = false;
            }
        } else {
            if (BuildConfig.DEBUG) Log.e("Tooriistad", "Kohaliku faili kustutamisel viga. failinimi==null!");
            retVal = false;
        }
        return retVal;
    }

}

