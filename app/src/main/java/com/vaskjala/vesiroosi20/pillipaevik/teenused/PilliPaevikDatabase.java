package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKord;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.kalender.HarjutuskordKirje;
import com.vaskjala.vesiroosi20.pillipaevik.kalender.KalendriKirje;
import com.vaskjala.vesiroosi20.pillipaevik.Teos;
import com.vaskjala.vesiroosi20.pillipaevik.aruanded.DetailiKirje;
import com.vaskjala.vesiroosi20.pillipaevik.kalender.PaevaKirje;

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
    private static final int DATABASE_VERSION = 8;
    // Database Name
    public static final String DATABASE_NAME = "PilliPaevik";

    private static final String CREATE_TABLE_TEOS = "CREATE TABLE " + Teos.Teosekirje.TABLE_NAME + "(" +
            Teos.Teosekirje._ID + " INTEGER PRIMARY KEY," +
            Teos.Teosekirje.COLUMN_NAME_NIMI + " TEXT," +
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

            String lisanaidisteos = "INSERT INTO " + Teos.Teosekirje.TABLE_NAME + " VALUES(1,'" +
                    context.getString(R.string.naidislugu) + "','" +
                    context.getString(R.string.looautor) + "','" +
                    context.getString(R.string.lookommentaar) + "', 5 , datetime('now') , 1 " +
                    ")";
            String lisanaidisharjutus1 = "INSERT INTO " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                    " VALUES(1, datetime('now'), 300, datetime('now','+5 minutes'),'" +
                    context.getString(R.string.harjutusekirjeldus1) + "', datetime('now'), 1 , null, null, null, null, 0" +
                    ")";
            String lisanaidisharjutus2 = "INSERT INTO " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                    " VALUES(2, datetime('now','+10 minutes'), 600, datetime('now','+20 minutes'),'" +
                    context.getString(R.string.harjutusekirjeldus2) + "', datetime('now'), 1 , null, null, null, null, 0" +
                    ")";
            String lisanaidisharjutus3 = "INSERT INTO " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                    " VALUES(3, datetime('now','+25 minutes'), 900, datetime('now','+40 minutes'),'" +
                    context.getString(R.string.harjutusekirjeldus3) + "', datetime('now'), 1 , null, null, null, null, 0" +
                    ")";
            db.execSQL(lisanaidisteos);
            db.execSQL(lisanaidisharjutus1);
            db.execSQL(lisanaidisharjutus2);
            db.execSQL(lisanaidisharjutus3);

        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "OnCreate:" + e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion <= 7) {
            try {
                Log.d(LOG, "onUpgrade oldVersion <= 7");
                db.execSQL("ALTER TABLE " + Teos.Teosekirje.TABLE_NAME + " RENAME TO TeosTMP");
                db.execSQL("ALTER TABLE " + HarjutusKord.Harjutuskordkirje.TABLE_NAME + " RENAME TO HarjutuskordTMP");
                db.execSQL("DROP INDEX IF EXISTS Teoseid");
                db.execSQL(CREATE_TABLE_TEOS);
                db.execSQL(CREATE_TABLE_HARJUTUSKORD);
                db.execSQL(CREATE_INDEX_TEOSEID);
                db.execSQL("INSERT INTO " + Teos.Teosekirje.TABLE_NAME + " SELECT * FROM TeosTMP");
                db.execSQL("INSERT INTO " + HarjutusKord.Harjutuskordkirje.TABLE_NAME + " SELECT * FROM HarjutuskordTMP");
                db.execSQL("DROP TABLE HarjutuskordTMP");
                db.execSQL("DROP TABLE TeosTMP");
            } catch (Exception e){
                Log.e(LOG, "onUpgrade:" + e.getMessage());
            }
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

    public HarjutusKord getHarjutus(int teosid, int harjutusid) {
        HarjutusKord retVal = null;
        Teos teos = getTeos(teosid);
        if(teos != null) {
            HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(context);
            if(harjutuskorradmap != null) {
                retVal = harjutuskorradmap.get(harjutusid);
            }
        }
        return retVal;
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
            }
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();
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
            }
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "KustutaTeos " + e);
        }
    }

    public int KustutaHarjutus(int teosid, int harjutusid){
        int deletedrows = 0;
        try {
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getWritableDatabase();
                deletedrows = db.delete(HarjutusKord.Harjutuskordkirje.TABLE_NAME, HarjutusKord.Harjutuskordkirje._ID+ "=" + harjutusid, null);
                Teos teos = teosedmap.get(teosid);
                if(teos != null){
                    teos.EemaldaHarjutusHulkadest(harjutusid);
                } else {
                    if(BuildConfig.DEBUG) Log.e("PilliPaevikDatabase","Teost ei leidu hulgas kui kustutatakse harjutust:" + harjutusid + " Teosid:" + teosid);
                }
                db.close();
                if(BuildConfig.DEBUG) Log.d("PilliPaevikDatabase","Kustuta teose Harjutus. Teosid:" + teosid + " Harjutus:" + harjutusid +
                        " Ridu kustutatud:" + deletedrows);
            }
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e(LOG, "KustutaHarjutus " + e);
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
                    teos.EemaldaHarjutuskorradHulkadest();
                } else {
                    if(BuildConfig.DEBUG) Log.e("PilliPaevikDatabase","Teost ei leidu hulgas kui harjutusi kustutatakse. Teosid:" + teosid);
                }
                db.close();
                if(BuildConfig.DEBUG) Log.d("PilliPaevikDatabase","Kustuta teose Harjutused. Teosid:" + teosid + " Ridu kustutatud:" + deletedrows);
            }
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();
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

    public int SalvestaHarjutusKord(HarjutusKord harjutuskord){

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
            }
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();
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
                    harjutusKord.Salvesta(context);
                    if(BuildConfig.DEBUG) Log.d(LOG, "Harjutuskorrale WebLink lisatud" + DriveIDMuutumatu);
                } else {
                    if(BuildConfig.DEBUG) Log.e(LOG, "Harjutuskorda ei leitud. WebLink lisamata. Driveid:" + DriveIDMuutumatu);
                }
                retVal = c.getInt(0);
                c.close();
                db.close();
            }
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();
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

        int[] retVal = new int[4];
        String selectParing = "SELECT SUM(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES +
                "), COUNT(*) FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                " WHERE " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + "=" + String.valueOf(teoseid);
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);

        Date now = new Date();
        String selectParingTana = "SELECT SUM(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES +
                "), COUNT(*) FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                " WHERE " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + "=" + String.valueOf(teoseid) + " AND date(" +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ") >= date('" + Tooriistad.KujundaKuupaev(now) +
                "') AND date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG +
                ") <= date('" + Tooriistad.KujundaKuupaev(now) + "')";
        if(BuildConfig.DEBUG) Log.d(LOG, selectParingTana);

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

                Cursor c1 = db.rawQuery(selectParingTana, null);
                // looping through all rows and adding to list
                if (c1.moveToFirst()) {
                    retVal[2] = c1.getInt(0);
                    retVal[3] = c1.getInt(1);
                    if(BuildConfig.DEBUG) Log.d("TeoseHarjutusKordad....", "Sekundeid täna:" + retVal[2] + " Kordi täna:" + retVal[3]);
                }
                c1.close();

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
                                Tooriistad.KujundaHarjutusteMinutid(context.getApplicationContext(), c.getInt(1) / 60));
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

    public List<HarjutusKord> LeiaDraivistPuuduvadFailid(){

        List<HarjutusKord> pList = new ArrayList<HarjutusKord>();
        String selectParing = "SELECT " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + ","
                + HarjutusKord.Harjutuskordkirje._ID + " FROM "
                + HarjutusKord.Harjutuskordkirje.TABLE_NAME + " WHERE " +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAIL + " IS NOT NULL AND " +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID + " IS NULL";
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                if (c.moveToFirst()) {
                    do {
                        HashMap<Integer, HarjutusKord> harjutuskorradmap = getTeos(c.getInt(0)).getHarjutuskorradmap(context);
                        HarjutusKord harjutusKord = harjutuskorradmap.get(c.getInt(1));
                        if(BuildConfig.DEBUG) Log.d("LeiaDraivistPuuduvadFa", "Harjutuskord: " + harjutusKord.toString());
                        pList.add(harjutusKord);
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("LeiaDraivistPuuduvadFa", "Ei suuda lugeda" + e.toString());
        }
        return pList;
    }

    public List<String> LeiaDraivistIlmaLingitaFailid(){

        List<String> pList = new ArrayList<String>();
        String selectParing = "SELECT " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID + " FROM "
                + HarjutusKord.Harjutuskordkirje.TABLE_NAME + " WHERE " +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAIL + " IS NOT NULL AND " +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID + " IS NOT NULL AND " +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEWEBLINK + " IS NULL";
        if(BuildConfig.DEBUG) Log.d(LOG, selectParing);
        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                // looping through all rows and adding to list
                if (c.moveToFirst()) {
                    do {
                        pList.add(c.getString(0));
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            if(BuildConfig.DEBUG) Log.e("LeiaDraivistIlma", "Ei suuda lugeda" + e.toString());
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

        String selectParing = "SELECT " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + ","+
                HarjutusKord.Harjutuskordkirje._ID + " FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                " WHERE date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG +
                ") >= date('" + Tooriistad.KujundaKuupaev(paevaKirje.kuupaev) +
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
                    paevaKirje.Harjutused = new ArrayList<HarjutuskordKirje>();
                    do {
                        if(BuildConfig.DEBUG) Log.d("KuupaevaHarjutusKorrad", c.getInt(0) + " " + c.getInt(1));
                        Teos teos = getTeos(c.getInt(0));
                        HarjutusKord harjutusKord = teos.getHarjutuskorradmap(context).get(c.getInt(1));
                        HarjutuskordKirje pPK = new HarjutuskordKirje(KalendriKirje.Tyyp.HARJUTUS, teos.getNimi(), harjutusKord, paevaKirje);
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

    public HashMap<Long, KalendriKirje> HarjutusteStatistikaPerioodisPaevaKaupa(Date algus, Date lopp){

        HashMap<Long, KalendriKirje> pList = new HashMap<Long, KalendriKirje>();
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
                        PaevaKirje kirje = new PaevaKirje(KalendriKirje.Tyyp.PAEV, "", pDate, c.getInt(1), c.getInt(2));
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
