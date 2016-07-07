package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.content.Intent;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.KustutaFailDraivistTeenus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;

import java.util.*;

/**
 * Created by mihkel on 2.05.2016.
 */
public class Teos implements Comparable<Teos> {

    private int id;
    private String nimi;
    private String autor;
    private String kommentaar;
    private short hinnang;
    private Date lisatudpaevikusse;
    private short kasutusviis;

    private List<HarjutusKord> Harjustuskorrad = null;
    private HashMap<Integer, HarjutusKord> Harjutuskorradmap = null;

    @Override
    public int compareTo(@NonNull Teos another) {
        return this.getNimi().compareTo(another.getNimi());
    }

    public static abstract class Teosekirje implements BaseColumns {
        public static final String TABLE_NAME = "Teos";
        public static final String COLUMN_NAME_NIMI = "nimi";
        public static final String COLUMN_NAME_AUTOR = "autor";
        public static final String COLUMN_NAME_KOMMENTAAR = "kommentaar";
        public static final String COLUMN_NAME_HINNANG = "hinnang";
        public static final String COLUMN_NAME_LISATUDPAEVIKUSSE = "lisatudpaevikusse";
        public static final String COLUMN_NAME_KASUTUSVIIS = "kasutusviis";
    }

    public Teos(){

    }

    private void LoadHarjustuskorrad(Context context) {
        Harjustuskorrad = new ArrayList<HarjutusKord>();
        Harjutuskorradmap = new HashMap<Integer, HarjutusKord>();
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(context);
        mPPManager.getAllHarjutuskorrad(this.id, this.Harjustuskorrad, this.Harjutuskorradmap);
    }

    public List<HarjutusKord> getHarjustuskorrad(Context context) {

        if(Harjustuskorrad == null)
            LoadHarjustuskorrad(context);
        return Harjustuskorrad;
    }

    public HashMap<Integer, HarjutusKord> getHarjutuskorradmap(Context context) {

        if(Harjutuskorradmap == null)
            LoadHarjustuskorrad(context);
        return Harjutuskorradmap;
    }

    public void EemaldaHarjutuskorradHulkadest() {
        if(Harjustuskorrad != null)
            Harjustuskorrad.clear();
        if(Harjutuskorradmap != null)
            Harjutuskorradmap.clear();
    }

    public void EemaldaHarjutusHulkadest(int harjutusid) {

        HarjutusKord harjutus = null;
        if(Harjutuskorradmap != null) {
            harjutus = Harjutuskorradmap.get(harjutusid);
            Harjutuskorradmap.remove(harjutusid);
            if(BuildConfig.DEBUG) Log.d("Teos","Eemaldasinme Harjutuse Mapist. Harjutus:"+ harjutusid);
        }
        if(Harjustuskorrad != null && harjutus != null){
            Harjustuskorrad.remove(harjutus);
            if(BuildConfig.DEBUG) Log.d("Teos","Eemaldasinme Harjutuse Setist. Harjutus:"+ harjutusid);
        }

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getKommentaar() {
        return kommentaar;
    }
    public void setKommentaar(String kommentaar) {
        this.kommentaar = kommentaar;
    }

    public short getHinnang() {
        return hinnang;
    }
    public void setHinnang(short hinnang) {
        this.hinnang = hinnang;
    }

    public Date getLisatudpaevikusse() {
        return lisatudpaevikusse;
    }
    public void setLisatudpaevikusse(Date lisatudpaevikusse) {
        this.lisatudpaevikusse = lisatudpaevikusse;
    }

    public short getKasutusviis() {
        return kasutusviis;
    }
    public void setKasutusviis(short kasutusviis) {
        this.kasutusviis = kasutusviis;
    }

    public void Salvesta(Context context){
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(context);
        mPPManager.SalvestaTeos(this);
    }
    public void Kustuta(Context context){
        KustutaHarjutusteFailid(context);
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(context);
        mPPManager.KustutaTeos(getId());
    }

    public void KustutaHarjutusteFailid(Context context){
        List<HarjutusKord> pHarjutused = getHarjustuskorrad(context);
        for(HarjutusKord pH : pHarjutused) {
            pH.KustutaFailid(context);
        }
    }


    public String toString(){
        return "ID:" + id + "Nimi:" + this.nimi + " Autor:" + this.autor +
                " Kommentaar:" + this.kommentaar + " Hinnang:" + this.hinnang +
                " Lisatud:" + this.lisatudpaevikusse + " Kasutusviis:" + this.kasutusviis;
    }
}
