package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKord;
import com.vaskjala.vesiroosi20.pillipaevik.PaevaKirje;
import com.vaskjala.vesiroosi20.pillipaevik.Teos;
import com.vaskjala.vesiroosi20.pillipaevik.aruanded.DetailiKirje;

import java.util.*;


/**
 * Created by mihkel on 2.05.2016.
 */
public class PilliPaevikDatabase extends SQLiteOpenHelper {

    // Sünkroniseerimiseks
    static final Object sPilliPaevikuLukk = new Object();

    private Context context;

    // Logcat tag
    private static final String LOG = "PilliPaevikDatabase";
    // Database Version
    private static final int DATABASE_VERSION = 7;
    // Database Name
    public static final String DATABASE_NAME = "PilliPaevik";

    private static final String CREATE_TABLE_TEOS = "CREATE TABLE " + Teos.Teosekirje.TABLE_NAME + "(" +
            Teos.Teosekirje._ID + " INTEGER PRIMARY KEY," +
            Teos.Teosekirje.COLUMN_NAME_NIMI + " TEXT UNIQUE," +
            Teos.Teosekirje.COLUMN_NAME_AUTOR + " TEXT," +
            Teos.Teosekirje.COLUMN_NAME_KOMMENTAAR + " TEXT," +
            Teos.Teosekirje.COLUMN_NAME_HINNANG + " INTEGER," +
            Teos.Teosekirje.COLUMN_NAME_LISATUDPAEVIKUSSE + " DATETIME," +
            Teos.Teosekirje.COLUMN_NAME_KASUTUSVIIS + " INTEGER" + ")";

    private static final String CREATE_TABLE_HARJUTUSKORD = "CREATE TABLE " + HarjutusKord.Harjutuskordkirje.TABLE_NAME + "(" +
            HarjutusKord.Harjutuskordkirje._ID + " INTEGER PRIMARY KEY," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + " DATETIME," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES + " INTEGER," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LOPUAEG + " DATETIME," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HARJUTUSEKIRJELDUS + " TEXT," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LISATUDPAEVIKUSSE + " DATETIME," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + " INTEGER," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAIL + " TEXT," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID + " TEXT," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEWEBLINK + " TEXT," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEIDMUUTUMATU + " TEXT," +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_WEBLINKARUANDELE + " INTEGER," +
            "FOREIGN KEY (" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + ") REFERENCES " +
            Teos.Teosekirje.TABLE_NAME + "(" + Teos.Teosekirje._ID + "))";

    private static final String CREATE_INDEX_TEOSEID = "CREATE INDEX IF NOT EXISTS Teoseid ON " +
            HarjutusKord.Harjutuskordkirje.TABLE_NAME + "(" +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + ")";

    private static final List<Teos> teosed = new ArrayList<Teos>();
    private static final HashMap<Integer, Teos> teosedmap = new HashMap<Integer, Teos>();

    public PilliPaevikDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(CREATE_TABLE_TEOS);
            db.execSQL(CREATE_TABLE_HARJUTUSKORD);
            db.execSQL(CREATE_INDEX_TEOSEID);
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "OnCreate:" + e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion <= 2)
            db.execSQL("ALTER TABLE Harjutuskord ADD COLUMN helifail TEXT");
        if(oldVersion <= 4) {
            db.execSQL("ALTER TABLE Harjutuskord ADD COLUMN helifailidriveid TEXT");
            db.execSQL("ALTER TABLE Harjutuskord ADD COLUMN helifailidriveweblink TEXT");
        }
        if(oldVersion <= 5) {
            db.execSQL("ALTER TABLE Harjutuskord ADD COLUMN helifailidriveidmuutumatu TEXT");
        }
        if(oldVersion <= 6) {
            db.execSQL("ALTER TABLE Harjutuskord ADD COLUMN weblinkaruandele INTEGER");
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @SuppressWarnings("SameReturnValue")
    public List<Teos> getAllTeosed(){

        try {
            if (teosed.isEmpty()) {
                String selectParing = "SELECT * FROM " + Teos.Teosekirje.TABLE_NAME + " ORDER BY nimi";
                if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
                synchronized (sPilliPaevikuLukk) {
                    SQLiteDatabase db = this.getReadableDatabase();
                    Cursor c = db.rawQuery(selectParing, null);

                    // looping through all rows and adding to list and map
                    if (c.moveToFirst()) {
                        do {
                            Teos teos = new Teos();
                            teos.setId(c.getInt((c.getColumnIndex(Teos.Teosekirje._ID))));
                            teos.setNimi((c.getString(c.getColumnIndex(Teos.Teosekirje.COLUMN_NAME_NIMI))));
                            teos.setAutor(c.getString(c.getColumnIndex(Teos.Teosekirje.COLUMN_NAME_AUTOR)));
                            teos.setKommentaar(c.getString(c.getColumnIndex(Teos.Teosekirje.COLUMN_NAME_KOMMENTAAR)));
                            teos.setHinnang(c.getShort((c.getColumnIndex(Teos.Teosekirje.COLUMN_NAME_HINNANG))));
                            teos.setLisatudpaevikusse(Tooriistad.KuupaevKellaAegStringist(c.getString(c.getColumnIndex(Teos.Teosekirje.COLUMN_NAME_LISATUDPAEVIKUSSE))));
                            teos.setKasutusviis(c.getShort(c.getColumnIndex(Teos.Teosekirje.COLUMN_NAME_KASUTUSVIIS)));
                            teosed.add(teos);
                            teosedmap.put(teos.getId(), teos);
                            if(BuildConfig.DEBUG) Log.d("getAllTeosed", teos.toString());
                        } while (c.moveToNext());
                    }
                    c.close();
                    db.close();
                }
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "getAllTeosed " + e);
        }
        return teosed;
    }
    @SuppressWarnings("SameReturnValue")
    private HashMap<Integer, Teos> getTeosedHash(){
        if(teosedmap.isEmpty())
            getAllTeosed();

        return teosedmap;
    }

    public Teos getTeos(int id){
        return getTeosedHash().get(id);
    }

    public int SalvestaTeos(Teos teos){

        int retVal = 0;

        List<Teos> teosed = getAllTeosed();
        boolean bNew = (teosedmap.get(teos.getId()) == null);
        try {
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Teos.Teosekirje.COLUMN_NAME_NIMI, teos.getNimi());
                values.put(Teos.Teosekirje.COLUMN_NAME_AUTOR, teos.getAutor());
                values.put(Teos.Teosekirje.COLUMN_NAME_KOMMENTAAR, teos.getKommentaar());
                values.put(Teos.Teosekirje.COLUMN_NAME_HINNANG, teos.getHinnang());
                values.put(Teos.Teosekirje.COLUMN_NAME_LISATUDPAEVIKUSSE, Tooriistad.KujundaKuupaevKellaaeg(teos.getLisatudpaevikusse()));
                values.put(Teos.Teosekirje.COLUMN_NAME_KASUTUSVIIS, teos.getKasutusviis());
                if (bNew)
                    retVal = (int) db.insert(Teos.Teosekirje.TABLE_NAME, null, values);
                else
                    retVal = db.update(Teos.Teosekirje.TABLE_NAME, values,
                            Teos.Teosekirje._ID + "=" + String.valueOf(teos.getId()), null);

                if (retVal > 0) {
                    if (bNew) {
                        teos.setId(retVal);
                        teosed.add(teos);
                        teosedmap.put(retVal, teos);
                        if(BuildConfig.DEBUG) Log.d(LOG, "Uus teos lisatud:" + String.valueOf(retVal));
                    } else
                        if(BuildConfig.DEBUG) Log.d(LOG, "Muudetud:" + String.valueOf(retVal) + " rida.");
                } else {
                    if(BuildConfig.DEBUG) Log.e(LOG, "Ei lisatud ega muudetud ühtegi Teose rida " + String.valueOf(retVal));
                }
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "Ei suutnud Teost salvestada " +  teos.toString() + " " + e.toString());
        }
        return retVal;
    }

    public void KustutaTeos(int teosid){
        try {
            synchronized (sPilliPaevikuLukk) {
                int deletedharjutused = KustutaTeoseHarjutused(teosid);
                SQLiteDatabase db = this.getWritableDatabase();
                int deletedrows = db.delete(Teos.Teosekirje.TABLE_NAME, Teos.Teosekirje._ID + "=" + teosid, null);
                Teos teos = teosedmap.get(teosid);
                teosed.remove(teos);
                teosedmap.remove(teosid);
                db.close();
                if(BuildConfig.DEBUG) Log.d("PilliPaevikDatabase","Kustuta teos:" + teosid + " Teose kustutatud:" +
                        deletedrows + " Harjutusi:" + deletedharjutused);
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "KustutaTeos " + e);
        }
    }

    public int KusututaHarjutus (int teosid, int harjutusid){
        int deletedrows = 0;
        try {
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getWritableDatabase();
                deletedrows = db.delete(HarjutusKord.Harjutuskordkirje.TABLE_NAME, HarjutusKord.Harjutuskordkirje._ID+ "=" + harjutusid, null);
                Teos teos = teosedmap.get(teosid);
                if(teos != null){
                    HashMap<Integer, HarjutusKord> pHarjutused = teos.getHarjutuskorradmap(context);
                    HarjutusKord pH = pHarjutused.get(harjutusid);
                    Intent intent = new Intent(context, KustutaFailDraivistTeenus.class);
                    intent.putExtra("driveid", pH.getHelifailidriveid());
                    context.startService(intent);
                    teos.EemaldaHarjutusHulkadest(harjutusid);
                } else {
                    if(BuildConfig.DEBUG) Log.e("PilliPaevikDatabase","Teost ei leidu hulgas kui kustutatakse harjutust:" + harjutusid + " Teosid:" + teosid);
                }
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
                if(BuildConfig.DEBUG) Log.d("PilliPaevikDatabase","Kustuta teose Harjutus. Teosid:" + teosid + " Harjutus:" + harjutusid +
                        " Ridu kustutatud:" + deletedrows);
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "KusututaHarjutus " + e);
        }

        return deletedrows;
    }

    private int KustutaTeoseHarjutused(int teosid){
        int deletedrows = 0;
        try {
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getWritableDatabase();
                deletedrows = db.delete(HarjutusKord.Harjutuskordkirje.TABLE_NAME, HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + "=" + teosid, null);
                Teos teos = teosedmap.get(teosid);
                if(teos != null){
                    List<HarjutusKord> pHarjutused = teos.getHarjustuskorrad(context);
                    for(HarjutusKord pH : pHarjutused) {
                        Intent intent = new Intent(context, KustutaFailDraivistTeenus.class);
                        intent.putExtra("driveid", pH.getHelifailidriveid());
                        context.startService(intent);
                    }
                    teos.EemaldaHarjutuskorradHulkadest();
                } else {
                    if(BuildConfig.DEBUG) Log.e("PilliPaevikDatabase","Teost ei leidu hulgas kui harjutusi kustutatakse. Teosid:" + teosid);
                }
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
                if(BuildConfig.DEBUG) Log.d("PilliPaevikDatabase","Kustuta teose Harjutused. Teosid:" + teosid + " Ridu kustutatud:" + deletedrows);
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "KustutaTeoseHarjutused " + e);
        }
        return deletedrows;
    }

    public boolean NimiUnikaalne( Teos teos, String nimi){
        boolean retVal = true;
        List<Teos> teosed = getAllTeosed();
        for (Teos teoshulgast : teosed){
            if(teoshulgast != teos){
                if(teoshulgast.getNimi().equals(nimi)){
                    retVal = false;
                    break;
                }
            }
        }
        return retVal;
    }

    public void getAllHarjutuskorrad(long teoseid, List<HarjutusKord> Harjustuskorrad,
                                     HashMap<Integer, HarjutusKord> Harjutuskorradmap){

        String selectParing = "SELECT * FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                " WHERE " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + "=" + String.valueOf(teoseid) +
                " ORDER BY " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + " DESC";
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);

        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                if (c.moveToFirst()) {
                    do {
                        HarjutusKord harjutuskord = new HarjutusKord();
                        harjutuskord.setId(c.getInt((c.getColumnIndex(HarjutusKord.Harjutuskordkirje._ID))));
                        harjutuskord.setAlgusaegEiArvuta(Tooriistad.KuupaevKellaAegStringist(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG))));
                        harjutuskord.setPikkussekundites(c.getInt(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES)));
                        harjutuskord.setLopuaegEiArvuta(Tooriistad.KuupaevKellaAegStringist(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LOPUAEG))));
                        harjutuskord.setHarjutusekirjeldus(c.getString((c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HARJUTUSEKIRJELDUS))));
                        harjutuskord.setLisatudpaevikusse(Tooriistad.KuupaevKellaAegStringist(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LISATUDPAEVIKUSSE))));
                        harjutuskord.setTeoseid(c.getInt(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID)));
                        harjutuskord.setHelifail(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAIL)));
                        harjutuskord.setHelifailidriveid(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID)));
                        harjutuskord.setHelifailidriveweblink(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEWEBLINK)));
                        harjutuskord.setHelifailidriveidmuutumatu(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEIDMUUTUMATU)));
                        harjutuskord.setWeblinkaruandele(c.getInt(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_WEBLINKARUANDELE)));
                        Harjustuskorrad.add(harjutuskord);
                        Harjutuskorradmap.put(harjutuskord.getId(), harjutuskord);
                        if(BuildConfig.DEBUG) Log.d("getAllHarjutuskorrad", harjutuskord.toString());
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("getAllHarjutuskorrad", "Ei suuda lugeda" + e.toString());
        }
    }

    public int SalvestaHarjutusKord(Context context, HarjutusKord harjutuskord){

        int retVal = 0;
        List<HarjutusKord> harjutuskorrad = getTeos(harjutuskord.getTeoseid()).getHarjustuskorrad(context);
        HashMap<Integer, HarjutusKord> harjutuskorradmap = getTeos(harjutuskord.getTeoseid()).getHarjutuskorradmap(context);

        boolean bNew = (harjutuskorradmap.get(harjutuskord.getId()) == null);
        try {
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG, Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getAlgusaeg()));
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES, harjutuskord.getPikkussekundites());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LOPUAEG, Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getLopuaeg()));
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HARJUTUSEKIRJELDUS, harjutuskord.getHarjutusekirjeldus());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LISATUDPAEVIKUSSE, Tooriistad.KujundaKuupaevKellaaeg(harjutuskord.getLisatudpaevikusse()));
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID, harjutuskord.getTeoseid());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAIL, harjutuskord.getHelifail());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID, harjutuskord.getHelifailidriveid());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEWEBLINK, harjutuskord.getHelifailidriveweblink());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEIDMUUTUMATU, harjutuskord.getHelifailidriveidmuutumatu());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_WEBLINKARUANDELE, harjutuskord.getWeblinkaruandele());

                if (bNew)
                    retVal = (int) db.insert(HarjutusKord.Harjutuskordkirje.TABLE_NAME, null, values);
                else
                    retVal = db.update(HarjutusKord.Harjutuskordkirje.TABLE_NAME, values,
                            HarjutusKord.Harjutuskordkirje._ID + "=" + String.valueOf(harjutuskord.getId()), null);

                if (retVal > 0) {
                    if (bNew) {
                        harjutuskord.setId(retVal);
                        harjutuskorrad.add(harjutuskord);
                        harjutuskorradmap.put(retVal, harjutuskord);
                        if(BuildConfig.DEBUG) Log.d(LOG, "Harjutuskord lisatud:" + harjutuskord.toString());
                    } else {
                        if(BuildConfig.DEBUG) Log.d(LOG, "Harjutuskord muudetud:" + harjutuskord.toString());
                    }
                } else {
                    if(BuildConfig.DEBUG) Log.e(LOG, "Ei lisatud ega muudetud ühtegi Harjutuskorrad rida " + String.valueOf(retVal));
                }
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "Ei suutnud salvestada " + e.toString());
        }
        return retVal;
    }

    public int SalvestaHarjutuskorraWebLink(String DriveIDMuutumatu, String WebLink){

        int retVal = 0;
        try {
            synchronized (sPilliPaevikuLukk) {
                String selectParing = "SELECT " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + ","
                        + HarjutusKord.Harjutuskordkirje._ID + " FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                        " WHERE " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEIDMUUTUMATU  + "='" +
                        DriveIDMuutumatu + "'";
                if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                if(c.moveToFirst()){
                    HashMap<Integer, HarjutusKord> harjutuskorradmap = getTeos(c.getInt(0)).getHarjutuskorradmap(context);
                    HarjutusKord harjutusKord = harjutuskorradmap.get(c.getInt(1));
                    harjutusKord.setHelifailidriveweblink(WebLink);
                    SalvestaHarjutusKord(context,harjutusKord);
                    if(BuildConfig.DEBUG) Log.d(LOG, "Harjutuskorrale WebLink lisatud" + DriveIDMuutumatu);
                } else {
                    if(BuildConfig.DEBUG) Log.e(LOG, "Harjutuskorda ei leitud. WebLink lisamata. Driveid:" + DriveIDMuutumatu);
                }
                retVal = c.getInt(0);
                c.close();
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "Ei suutnud Harjutuskorda muuta" + e.toString());
        }
        return retVal;
    }

    public int ArvutaPerioodiMinutid (Date algus, Date lopp){

        int retVal = 0;
        String selectParing = "SELECT SUM(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES + ") FROM "
                + HarjutusKord.Harjutuskordkirje.TABLE_NAME + " WHERE date(" +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ") >= date('" + Tooriistad.KujundaKuupaev(algus) +
                "') AND date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG +
                ") <= date('" + Tooriistad.KujundaKuupaev(lopp) + "')";

        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
        try {
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                c.moveToFirst();
                retVal = c.getInt(0);
                retVal = (int)Math.ceil((double)retVal / 60.0);
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "ArvutaPerioodiMinutid " + e);
        }
        if(BuildConfig.DEBUG) Log.d("ArvutaPerioodiMinutid", "retVal:" + retVal);
        return retVal;

    }

    public int[] TeoseHarjutusKordadeStatistika(int teoseid){

        int[] retVal = new int[2];
        String selectParing = "SELECT SUM(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES +
                "), COUNT(*) FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                " WHERE " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + "=" + String.valueOf(teoseid);
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);

        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                // looping through all rows and adding to list
                if (c.moveToFirst()) {
                    retVal[0] = c.getInt(0);
                    retVal[1] = c.getInt(1);
                    if(BuildConfig.DEBUG) Log.d("TeoseHarjutusKordad....", "Sekundeid:" + retVal[0] + " Kordi:" + retVal[1]);
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("getAllHarjutuskorrad", "Ei suuda lugeda" + e.toString());
        }
        return retVal;
    }

    // See on aruande jaoks
    public List<String> HarjutusKordadeStatistikaPerioodis(Date algus, Date lopp){

        List<String> pList = new ArrayList<String>();
        String selectParing = "SELECT " + Teos.Teosekirje.COLUMN_NAME_NIMI + ",SUM("+
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES +") FROM "
                + HarjutusKord.Harjutuskordkirje.TABLE_NAME + "," + Teos.Teosekirje.TABLE_NAME + " WHERE " +
                Teos.Teosekirje.TABLE_NAME + "." + Teos.Teosekirje._ID + "=" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + " AND date(" +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ") >= date('" + Tooriistad.KujundaKuupaev(algus) +
                "') AND date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG +
                ") <= date('" + Tooriistad.KujundaKuupaev(lopp) + "') GROUP BY " + Teos.Teosekirje.COLUMN_NAME_NIMI;
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                // looping through all rows and adding to list
                if (c.moveToFirst()) {
                    do {
                        String formaat = "%-30s%s";
                        String kirje = String.format(formaat,c.getString(0),
                                Tooriistad.KujundaHarjutusteMinutid(c.getInt(1) / 60));
                        pList.add(kirje);
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("TeoseHarjutusKordade...", "Ei suuda lugeda" + e.toString());
        }
        return pList;
    }

    // See on aruande jaoks
    public List<DetailiKirje> HarjutusKorradPerioodis(Date algus, Date lopp){

        List<DetailiKirje> pList = new ArrayList<DetailiKirje>();
        String selectParing = "SELECT " + Teos.Teosekirje.COLUMN_NAME_NIMI + ","+
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ","+
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES + "," +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEWEBLINK + "," +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_WEBLINKARUANDELE + " FROM "
                + HarjutusKord.Harjutuskordkirje.TABLE_NAME + "," + Teos.Teosekirje.TABLE_NAME + " WHERE " +
                Teos.Teosekirje.TABLE_NAME + "." + Teos.Teosekirje._ID + "=" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + " AND date(" +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ") >= date('" + Tooriistad.KujundaKuupaev(algus) +
                "') AND date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG +
                ") <= date('" + Tooriistad.KujundaKuupaev(lopp) + "') ORDER BY " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG;
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                // looping through all rows and adding to list
                if (c.moveToFirst()) {
                    do {
                        DetailiKirje pDK = new DetailiKirje(c.getString(0),
                                Tooriistad.KuupaevStringist(c.getString(1)), c.getInt(2),
                                c.getString(3), c.getInt(4));
                        pList.add(pDK);
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("TeoseHarjutusKordade...", "Ei suuda lugeda" + e.toString());
        }
        return pList;
    }

    public void KuupaevaHarjutusKorrad(PaevaKirje paevaKirje){

        String selectParing = "SELECT " + Teos.Teosekirje.COLUMN_NAME_NIMI + ","+
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES + ","+
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID + ","+
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + ","+
                HarjutusKord.Harjutuskordkirje.TABLE_NAME + "." + HarjutusKord.Harjutuskordkirje._ID + " FROM "
                + HarjutusKord.Harjutuskordkirje.TABLE_NAME + "," + Teos.Teosekirje.TABLE_NAME + " WHERE " +
                Teos.Teosekirje.TABLE_NAME + "." + Teos.Teosekirje._ID + "=" +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + " AND date(" +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ") >= date('" + Tooriistad.KujundaKuupaev(paevaKirje.kuupaev) +
                "') AND date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG +
                ") <= date('" + Tooriistad.KujundaKuupaev(paevaKirje.kuupaev) +
                "') ORDER BY " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + " DESC";
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                // looping through all rows and adding to list
                paevaKirje.bAndmebaasistLaetud = true;
                if (c.moveToFirst()) {
                    paevaKirje.Harjutused = new ArrayList<PaevaKirje>();
                    do {
                        if(BuildConfig.DEBUG) Log.e("KuupaevaHarjutusKorrad", c.getString(0) + " " + c.getInt(1) + " " + c.getInt(2));
                        PaevaKirje pPK = new PaevaKirje(paevaKirje.kuupaev, paevaKirje.kordadearv, paevaKirje.pikkussekundites);
                        pPK.Teos = c.getString(0);
                        pPK.harjutusepikkus = c.getInt(1);
                        pPK.DriveId = c.getString(2);
                        pPK.teosid = c.getInt(3);
                        pPK.harjutusid = c.getInt(4);
                        pPK.bPeaKirje = false;
                        paevaKirje.Harjutused.add(pPK);
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("KuupaevaHarjutusKorrad", "Ei suuda lugeda" + e.toString());
        }
    }

    public HashMap<Long, PaevaKirje> HarjutusteStatistikaPerioodisPaevaKaupa(Date algus, Date lopp){

        HashMap<Long, PaevaKirje> pList = new HashMap<Long, PaevaKirje>();
        String selectParing = "SELECT date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + "), COUNT(*),SUM("+
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES +") FROM "
                + HarjutusKord.Harjutuskordkirje.TABLE_NAME + " WHERE date(" +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ") >= date('" + Tooriistad.KujundaKuupaev(algus) +
                "') AND date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG +
                ") <= date('" + Tooriistad.KujundaKuupaev(lopp) + "') " +
                "GROUP BY date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ")";
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                // looping through all rows and adding to list
                if (c.moveToFirst()) {
                    do {
                        Date pDate = Tooriistad.KuupaevStringist(c.getString(0));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(pDate);
                        PaevaKirje kirje = new PaevaKirje(pDate, c.getInt(1), c.getInt(2));
                        pList.put(Long.valueOf(cal.getTimeInMillis()),kirje);
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("TeoseHarjutusKordade...", "Ei suuda lugeda" + e.toString());
        }
        return pList;
    }

    public void PilliPaevikTestMeetod(){
        String selectParing = "SELECT strftime('%Y',algusaeg), strftime('%m',algusaeg), strftime('%W',algusaeg) FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME;

        try{

            if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery(selectParing, null);
            // looping through all rows and adding to list
            if (c.moveToFirst()) {

                do {
                   if(BuildConfig.DEBUG) Log.d("getAllHarjutuskorrad",c.getString(0) + " " + c.getString(1) + c.getString(2));
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("PilliPaevikTestMeetod", "Ei suuda lugeda" + e.toString());
        }
    }
}
