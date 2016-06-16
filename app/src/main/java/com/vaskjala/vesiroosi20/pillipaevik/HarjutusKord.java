package com.vaskjala.vesiroosi20.pillipaevik;

import android.provider.BaseColumns;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

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
    private String helifail;
    private String helifailidriveid;
    private String helifailidriveweblink;

    public static abstract class Harjutuskordkirje implements BaseColumns {
        public static final String TABLE_NAME = "Harjutuskord";
        public static final String COLUMN_NAME_ALGUSAEG = "algusaeg";
        public static final String COLUMN_NAME_PIKKUSSEKUNDITES = "pikkussekundites";
        public static final String COLUMN_NAME_LOPUAEG = "lopuaeg";
        public static final String COLUMN_NAME_HARJUTUSEKIRJELDUS = "harjutusekirjeldus";
        public static final String COLUMN_NAME_LISATUDPAEVIKUSSE = "lisatudpaevikusse";
        public static final String COLUMN_NAME_TEOSEID = "teoseid";
        public static final String COLUMN_NAME_HELIFAIL = "helifail";
        public static final String COLUMN_NAME_HELIFAILIDRIVEID = "helifailidriveid";
        public static final String COLUMN_NAME_HELIFAILIDRIVEWEBLINK = "helifailidriveweblink";
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
    private void TeavitaAlguseMuutusest(){
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
    private void TeavitaLopuMuutusest(){
        Log.d("Harjutuskord","lopuaeg muutus");

        if(getAlgusaeg() == null) {
            setAlgusaeg(getLopuaeg());
        }
        else {
            if(getLopuaeg().before(getAlgusaeg()))
                setAlgusaeg(getLopuaeg());
        }
    }



    public int getPikkussekundites() {
        return pikkussekundites;
    }
    public void setPikkussekundites(int pikkussekundites) {
        this.pikkussekundites = pikkussekundites;
    }
    private void VarskendaPikkusSekundites(){
        setPikkussekundites(ArvutaPikkusSekundites());
    }
    private int ArvutaPikkusSekundites(){
        int retVal;
        Calendar c = Calendar.getInstance();

        c.setTime(getAlgusaeg());
        long algus = c.getTimeInMillis();

        c.setTime(getLopuaeg());
        long lopp = c.getTimeInMillis();

        retVal = (int)((lopp-algus)/(1000));
        return retVal;
    }

    public int getPikkusminutites(){
        return (int)Math.ceil((double)getPikkussekundites() / 60.0);
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

    public int getTeoseid() {
        return teoseid;
    }
    public void setTeoseid(int teoseid) {
        this.teoseid = teoseid;
    }

    public String getHelifail() {
        return helifail;
    }
    public void setHelifail(String helifail) {
        this.helifail = helifail;
    }

    public String getHelifailidriveid() {
        return helifailidriveid;
    }
    public void setHelifailidriveid(String helifailidriveid) {
        this.helifailidriveid = helifailidriveid;
    }

    public String getHelifailidriveweblink() {
        return helifailidriveweblink;
    }
    public void setHelifailidriveweblink(String helifailidriveweblink) {
        this.helifailidriveweblink = helifailidriveweblink;
    }

    public String MoodustaFailiNimi(){
        return String.valueOf(getTeoseid()) + "_" + String.valueOf(getId()) + "_" +
                Tooriistad.KujundaKuupaevKellaaegFailiNimi(new Date()) + ".mp4";
    }

    public String toString(){
        return "ID:" + this.id + "Algusaeg:" + this.algusaeg + " Pikkus:" + this.pikkussekundites +
                " Lopuaeg:" + this.lopuaeg + " Kirjeldus:" + this.harjutusekirjeldus +
                " Lisatud:" + this.lisatudpaevikusse + " Teoseid:" + this.teoseid + " Helifail:" + this.helifail +
                " DriveID:" + helifailidriveid + " WebLink:" + helifailidriveweblink;
    }
}
