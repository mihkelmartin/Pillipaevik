package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.*;


/**
 * Created by mihkel on 2.05.2016.
 */
public class PilliPaevikDatabase extends SQLiteOpenHelper {

    // Sünkroniseerimiseks
    static final Object sPilliPaevikuLukk = new Object();

    private static Context context;

    // Logcat tag
    private static final String LOG = "PilliPaevikDatabase";
    // Database Version
    private static final int DATABASE_VERSION = 5;
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
            "FOREIGN KEY (" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + ") REFERENCES " +
            Teos.Teosekirje.TABLE_NAME + "(" + Teos.Teosekirje._ID + "))";

    private static final String CREATE_INDEX_TEOSEID = "CREATE INDEX IF NOT EXISTS Teoseid ON " +
            HarjutusKord.Harjutuskordkirje.TABLE_NAME + "(" +
            HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + ")";

    private static final List<Teos> teosed = new ArrayList<Teos>();
    private static final HashMap<Integer, Teos> teosedmap = new HashMap<Integer, Teos>();

    public static void setContext(Context context) {
        PilliPaevikDatabase.context = context;
    }

    public PilliPaevikDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(CREATE_TABLE_TEOS);
            db.execSQL(CREATE_TABLE_HARJUTUSKORD);
            db.execSQL(CREATE_INDEX_TEOSEID);
        } catch (Exception e){
            Log.e(LOG, "OnCreate:" + e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion == 2)
            db.execSQL("ALTER TABLE Harjutuskord ADD COLUMN helifail TEXT");
        if(oldVersion == 3 || oldVersion == 4) {
            db.execSQL("ALTER TABLE Harjutuskord ADD COLUMN helifailidriveid TEXT");
            db.execSQL("ALTER TABLE Harjutuskord ADD COLUMN helifailidriveweblink TEXT");
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
                Log.d(LOG, selectParing);
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
                            teos.setLisatudpaevikusse(c.getString(c.getColumnIndex(Teos.Teosekirje.COLUMN_NAME_LISATUDPAEVIKUSSE)));
                            teos.setKasutusviis(c.getShort(c.getColumnIndex(Teos.Teosekirje.COLUMN_NAME_KASUTUSVIIS)));
                            teosed.add(teos);
                            teosedmap.put(teos.getId(), teos);
                            Log.d("getAllTeosed", teos.toString());
                        } while (c.moveToNext());
                    }
                    c.close();
                    db.close();
                }
            }
        } catch (Exception e){
            Log.e(LOG, "getAllTeosed " + e);
        }
        return teosed;
    }

    @SuppressWarnings("SameReturnValue")
    private HashMap<Integer, Teos> getTeosedHash(){
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
                values.put(Teos.Teosekirje.COLUMN_NAME_LISATUDPAEVIKUSSE, teos.getLisatudpaevikusseAsString());
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
                        Log.d(LOG, "Uus teos lisatud:" + String.valueOf(retVal));
                    } else
                        Log.d(LOG, "Muudetud:" + String.valueOf(retVal) + " rida.");
                } else {
                    Log.e(LOG, "Ei lisatud ega muudetud ühtegi Teose rida " + String.valueOf(retVal));
                }
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
            }
        } catch (Exception e){
            System.out.println("Ei suutnud salvestada " +  teos.toString() + " " + e.toString());
        }
        return retVal;
    }

    public void KustutaTeos(int teosid){
        try {
            synchronized (sPilliPaevikuLukk) {
                int deletedharjutused = KusututaTeoseHarjutused(teosid);
                SQLiteDatabase db = this.getWritableDatabase();
                int deletedrows = db.delete(Teos.Teosekirje.TABLE_NAME, Teos.Teosekirje._ID + "=" + teosid, null);
                Teos teos = teosedmap.get(teosid);
                teosed.remove(teos);
                teosedmap.remove(teosid);
                db.close();
                Log.d("PilliPaevikDatabase","Kustuta teos:" + teosid + " Teose kustutatud:" +
                        deletedrows + " Harjutusi:" + deletedharjutused);
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
            }
        } catch (Exception e){
            Log.e(LOG, "KustutaTeos " + e);
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
                    if(pH.getHelifailidriveid() != null && !pH.getHelifailidriveid().isEmpty()) {
                        GoogleDriveUhendus mGDU = GoogleDriveUhendus.getInstance();
                        mGDU.setmDriveActivity(null);
                        mGDU.KustutaDriveFail(pH.getHelifailidriveid());
                    }

                    teos.clearHarjutus(harjutusid);
                } else {
                    Log.e("PilliPaevikDatabase","Teost ei leidu hulgas kui kustutatakse harjutust:" + harjutusid + " Teosid:" + teosid);
                }
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
                Log.d("PilliPaevikDatabase","Kustuta teose Harjutus. Teosid:" + teosid + " Harjutus:" + harjutusid +
                        " Ridu kustutatud:" + deletedrows);
            }
        } catch (Exception e){
            Log.e(LOG, "KusututaHarjutus " + e);
        }

        return deletedrows;
    }

    private int KusututaTeoseHarjutused(int teosid){
        int deletedrows = 0;
        try {
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getWritableDatabase();
                deletedrows = db.delete(HarjutusKord.Harjutuskordkirje.TABLE_NAME, HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + "=" + teosid, null);
                Teos teos = teosedmap.get(teosid);
                if(teos != null){
                    // Kustuta Draivist failid
                    GoogleDriveUhendus mGDU = GoogleDriveUhendus.getInstance();
                    mGDU.setmDriveActivity(null);
                    List<HarjutusKord> pHarjutused = teos.getHarjustuskorrad(context);
                    for(HarjutusKord pH : pHarjutused) {
                        if(pH.getHelifailidriveid() != null && !pH.getHelifailidriveid().isEmpty()) {
                            mGDU.KustutaDriveFail(pH.getHelifailidriveid());
                        }
                    }
                    teos.clearHarjutuskorrad();
                } else {
                    Log.e("PilliPaevikDatabase","Teost ei leidu hulgas kui harjutusi kustutatakse. Teosid:" + teosid);
                }
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
                Log.d("PilliPaevikDatabase","Kustuta teose Harjutused. Teosid:" + teosid + " Ridu kustutatud:" + deletedrows);
            }
        } catch (Exception e){
            Log.e(LOG, "KusututaTeoseHarjutused " + e);
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
        Log.d(LOG, selectParing);

        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                if (c.moveToFirst()) {
                    do {
                        HarjutusKord harjutuskord = new HarjutusKord();
                        harjutuskord.setId(c.getInt((c.getColumnIndex(HarjutusKord.Harjutuskordkirje._ID))));
                        harjutuskord.setAlgusaeg((c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG))));
                        harjutuskord.setPikkussekundites(c.getInt(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES)));
                        harjutuskord.setLopuaeg(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LOPUAEG)));
                        harjutuskord.setHarjutusekirjeldus(c.getString((c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HARJUTUSEKIRJELDUS))));
                        harjutuskord.setLisatudpaevikusse(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LISATUDPAEVIKUSSE)));
                        harjutuskord.setTeoseid(c.getInt(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID)));
                        harjutuskord.setHelifail(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAIL)));
                        harjutuskord.setHelifailidriveid(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID)));
                        harjutuskord.setHelifailidriveweblink(c.getString(c.getColumnIndex(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEWEBLINK)));
                        Harjustuskorrad.add(harjutuskord);
                        Harjutuskorradmap.put(harjutuskord.getId(), harjutuskord);
                        Log.d("getAllHarjutuskorrad", harjutuskord.toString());
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            Log.e("getAllHarjutuskorrad", "Ei suuda lugeda" + e.toString());
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
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG, harjutuskord.getAlgusaegAsString());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES, harjutuskord.getPikkussekundites());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LOPUAEG, harjutuskord.getLopuaegAsString());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HARJUTUSEKIRJELDUS, harjutuskord.getHarjutusekirjeldus());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_LISATUDPAEVIKUSSE, harjutuskord.getLisatudpaevikusseAsString());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID, harjutuskord.getTeoseid());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAIL, harjutuskord.getHelifail());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEID, harjutuskord.getHelifailidriveid());
                values.put(HarjutusKord.Harjutuskordkirje.COLUMN_NAME_HELIFAILIDRIVEWEBLINK, harjutuskord.getHelifailidriveweblink());

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
                        Log.d(LOG, "Harjutuskord lisatud:" + harjutuskord.toString());
                    } else {
                        Log.d(LOG, "Harjutuskord muudetud:" + harjutuskord.toString());
                    }
                } else {
                    Log.e(LOG, "Ei lisatud ega muudetud ühtegi Harjutuskorrad rida " + String.valueOf(retVal));
                }
                db.close();
                BackupManager backupManager = new BackupManager(context);
                backupManager.dataChanged();
            }
        } catch (Exception e){
            System.out.println("Ei suutnud salvestada " + e.toString());
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

        Log.d(LOG, selectParing);
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
            Log.e(LOG, "ArvutaPerioodiMinutid " + e);
        }
        Log.d("ArvutaPerioodiMinutid", "retVal:" + retVal);
        return retVal;

    }

    public int[] TeoseHarjutusKordadeStatistika(int teoseid){

        int[] retVal = new int[2];
        String selectParing = "SELECT SUM(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES +
                "), COUNT(*) FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME +
                " WHERE " + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + "=" + String.valueOf(teoseid);
        Log.d(LOG, selectParing);

        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                // looping through all rows and adding to list
                if (c.moveToFirst()) {
                    retVal[0] = c.getInt(0);
                    retVal[1] = c.getInt(1);
                    Log.d("TeoseHarjutusKordad....", "Sekundeid:" + retVal[0] + " Kordi:" + retVal[1]);
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            Log.e("getAllHarjutuskorrad", "Ei suuda lugeda" + e.toString());
        }
        return retVal;
    }

    public List<String> HarjutusKordadeStatistikaPerioodis(Date algus, Date lopp){

        List<String> pList = new ArrayList<String>();
        String selectParing = "SELECT " + Teos.Teosekirje.COLUMN_NAME_NIMI + ",SUM("+
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_PIKKUSSEKUNDITES +") FROM "
                + HarjutusKord.Harjutuskordkirje.TABLE_NAME + "," + Teos.Teosekirje.TABLE_NAME + " WHERE " +
                Teos.Teosekirje.TABLE_NAME + "." + Teos.Teosekirje._ID + "=" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_TEOSEID + " AND (" +
                HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG + ") >= date('" + Tooriistad.KujundaKuupaev(algus) +
                "') AND date(" + HarjutusKord.Harjutuskordkirje.COLUMN_NAME_ALGUSAEG +
                ") <= date('" + Tooriistad.KujundaKuupaev(lopp) + "') GROUP BY " + Teos.Teosekirje.COLUMN_NAME_NIMI;
        Log.d(LOG, selectParing);
        try{
            synchronized (sPilliPaevikuLukk) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery(selectParing, null);
                // looping through all rows and adding to list
                if (c.moveToFirst()) {
                    do {
                        String kirje = "<tr><td>" + c.getString(0) + "</td><td>" +
                                Tooriistad.KujundaHarjutusteMinutid(c.getInt(1) / 60) + "</td></tr>";
                        pList.add(kirje);
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            }
        } catch (Exception e){
            Log.e("TeoseHarjutusKordade...", "Ei suuda lugeda" + e.toString());
        }
        return pList;
    }

    public void PilliPaevikTestMeetod(){
        String selectParing = "SELECT strftime('%Y',algusaeg), strftime('%m',algusaeg), strftime('%W',algusaeg) FROM " + HarjutusKord.Harjutuskordkirje.TABLE_NAME;

        try{

            Log.d(LOG, selectParing);
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery(selectParing, null);
            // looping through all rows and adding to list
            if (c.moveToFirst()) {

                do {
                   Log.d("getAllHarjutuskorrad",c.getString(0) + " " + c.getString(1) + c.getString(2));
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e){
            Log.e("PilliPaevikTestMeetod", "Ei suuda lugeda" + e.toString());
        }
    }
}
