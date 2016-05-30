package com.vaskjala.vesiroosi20.pillipaevik;

import android.content.Context;
import android.provider.BaseColumns;
import android.util.Log;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class HarjutusKord {
    private int id;
    private Date algusaeg;
    private int pikkussekundites;
    private Date lopuaeg;
    private String harjutusekirjeldus;
    private Date lisatudpaevikusse;
    private int teoseid;

    public static abstract class Harjutuskordkirje implements BaseColumns {
        public static final String TABLE_NAME = "Harjutuskord";
        public static final String COLUMN_NAME_ALGUSAEG = "algusaeg";
        public static final String COLUMN_NAME_PIKKUSSEKUNDITES = "pikkussekundites";
        public static final String COLUMN_NAME_LOPUAEG = "lopuaeg";
        public static final String COLUMN_NAME_HARJUTUSEKIRJELDUS = "harjutusekirjeldus";
        public static final String COLUMN_NAME_LISATUDPAEVIKUSSE = "lisatudpaevikusse";
        public static final String COLUMN_NAME_TEOSEID = "teoseid";
    }

    public HarjutusKord (){

    }

    public HarjutusKord (int teosid){
        Date now = Tooriistad.HetkeKuupaevNullitudSekunditega();
        setTeoseid(teosid);
        setAlgusaeg(now);
        setLopuaeg(now);
        setLisatudpaevikusse(now);
        setPikkussekundites(0);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Date getAlgusaeg() {
        return algusaeg;
    }
    public String getAlgusaegAsString(){
        String result = "";
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(this.algusaeg != null)
            result = format.format(this.algusaeg);

        return result;
    }
    public void setAlgusaeg(String algusaeg) {
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            setAlgusaegEiArvuta(format.parse(algusaeg));
        } catch (ParseException pe) {
            System.out.println("ERROR: could not parse date in string \"" +
                    algusaeg + "\"");
        }
    }
    public void setAlgusaeg(Date algusaeg) {
        boolean bAlgusMuutus = true;
        if(this.algusaeg != null)
            bAlgusMuutus = this.algusaeg.compareTo(algusaeg) != 0;
        this.algusaeg = algusaeg;

        if(bAlgusMuutus){
            TeavitaAlguseMuutusest();
            VarskendaPikkusSekundites();
        }

    }
    public void setAlgusaegEiArvuta(Date algusaeg) {
        this.algusaeg = algusaeg;
    }

    public void TeavitaAlguseMuutusest(){
        Log.d("Harjutuskord","algusaeg muutus");

        if(getLopuaeg() == null) {
            setLopuaeg(getAlgusaeg());
        }
        else {
            if(getAlgusaeg().after(getLopuaeg()))
                setLopuaeg(getAlgusaeg());
        }
    }

    public Date getLopuaeg() {
        return lopuaeg;
    }
    public String getLopuaegAsString(){
        String result = "";
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(this.lopuaeg != null)
            result = format.format(this.lopuaeg);

        return result;
    }
    public void setLopuaeg(Date lopuaeg) {

        boolean bLopuMuutus = true;
        if(this.lopuaeg != null)
            bLopuMuutus = this.lopuaeg.compareTo(lopuaeg) != 0;
        this.lopuaeg = lopuaeg;

        if(bLopuMuutus) {
            TeavitaLopuMuutusest();
            VarskendaPikkusSekundites();
        }
    }
    public void setLopuaegEiArvuta(Date lopuaeg) {
        this.lopuaeg = lopuaeg;
    }

    public void TeavitaLopuMuutusest(){
        Log.d("Harjutuskord","lopuaeg muutus");

        if(getAlgusaeg() == null) {
            setAlgusaeg(getLopuaeg());
        }
        else {
            if(getLopuaeg().before(getAlgusaeg()))
                setAlgusaeg(getLopuaeg());
        }
    }


    public void setLopuaeg(String lopuaeg) {
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            setLopuaegEiArvuta(format.parse(lopuaeg));
        } catch (ParseException pe) {
            System.out.println("ERROR: could not parse date in string \"" +
                    lopuaeg + "\"");
        }
    }

    public int getPikkussekundites() {
        return pikkussekundites;
    }
    public void setPikkussekundites(int pikkussekundites) {
        this.pikkussekundites = pikkussekundites;
    }
    public void VarskendaPikkusSekundites(){

        setPikkussekundites(ArvutaPikkusSekundites());

    }

    public int getPikkusminutites(){
        return (int)Math.ceil((double)getPikkussekundites() / 60.0);
    }
    public int ArvutaPikkusSekundites(){
        int retVal = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(getAlgusaeg());
        long algus = c.getTimeInMillis();
        c.setTime(getLopuaeg());
        long lopp = c.getTimeInMillis();
        retVal = (int)((lopp-algus)/(1000));
        return retVal;
    }
    public int ArvutaPikkusMinutites(){
        return (int)(ArvutaPikkusSekundites() / 60.0);
    }

    public String getHarjutusekirjeldus() {
        return harjutusekirjeldus;
    }
    public void setHarjutusekirjeldus(String harjutusekirjeldus) {
        this.harjutusekirjeldus = harjutusekirjeldus;
    }

    public Date getLisatudpaevikusse() {
        return lisatudpaevikusse;
    }
    public String getLisatudpaevikusseAsString(){
        String result = "";
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(lisatudpaevikusse != null)
            result = format.format(lisatudpaevikusse);

        return result;
    }
    public void setLisatudpaevikusse(Date lisatudpaevikusse) {
        this.lisatudpaevikusse = lisatudpaevikusse;
    }
    public void setLisatudpaevikusse(String lisatudpaevikusse) {
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.lisatudpaevikusse = format.parse(lisatudpaevikusse);
        } catch (ParseException pe) {
            System.out.println("ERROR: could not parse date in string \"" +
                    lisatudpaevikusse + "\"");
        }
    }

    public int getTeoseid() {
        return teoseid;
    }
    public void setTeoseid(int teoseid) {
        this.teoseid = teoseid;
    }

    public String toString(){
        String retVal = "ID:" + this.id + "Algusaeg:" + this.algusaeg + " Pikkus:" + this.pikkussekundites +
                " Lopuaeg:" + this.lopuaeg + " Kirjeldus:" + this.harjutusekirjeldus +
                " Lisatud:" + this.lisatudpaevikusse + " Teoseid:" + this.teoseid;

        return retVal;
    }
}
