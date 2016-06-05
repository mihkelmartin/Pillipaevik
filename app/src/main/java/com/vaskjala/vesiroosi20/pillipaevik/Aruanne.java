package com.vaskjala.vesiroosi20.pillipaevik;



import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mihkel on 5.06.2016.
 */


public class Aruanne {

    public final static String ReaVahetus = System.getProperty("line.separator");
    public String aruandekogutekst;

    private String aruandenimi;
    private String aruandeperioodinimi;
    private Date perioodialgus;
    private Date perioodilopp;
    private int perioodipikkus;

    private String minueesnimi;
    private String minuperenimi;
    private String opilaseinstrument;
    private String opetajaeesnimi;
    private String opetajaperenimi;
    private String opetajaepost;
    private int paevasharjutada;

    public Aruanne(Context context, String aruandenimi){

        this.aruandenimi = aruandenimi;
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.seadete_fail), MODE_PRIVATE);
        this.minueesnimi = sharedPref.getString("minueesnimi","");
        this.minuperenimi = sharedPref.getString("minuperenimi","");
        this.opilaseinstrument = sharedPref.getString("minuinstrument","");
        this.opetajaeesnimi = sharedPref.getString("opetajaeesnimi","");
        this.opetajaperenimi = sharedPref.getString("opetajaperenimi","");
        this.opetajaepost = sharedPref.getString("opetajaepost","");
        this.paevasharjutada = sharedPref.getInt("paevasharjutada",0);
    }

    public void setAruandenimi(String aruandenimi) {
        this.aruandenimi = aruandenimi;
    }

    public void setAruandeperioodinimi(String aruandeperioodinimi) {
        this.aruandeperioodinimi = aruandeperioodinimi;
    }

    public int getPerioodipikkus() {
        perioodipikkus = Tooriistad.KaheKuupaevaVahePaevades(perioodialgus, perioodilopp);
        return perioodipikkus;
    }

    public Date getPerioodialgus() {
        return perioodialgus;
    }

    public void setPerioodialgus(Date perioodialgus) {
        this.perioodialgus = perioodialgus;
    }

    public Date getPerioodilopp() {
        return perioodilopp;
    }

    public void setPerioodilopp(Date perioodilopp) {
        this.perioodilopp = perioodilopp;
    }

    public String getOpetajaepost() {
        return opetajaepost;
    }

    public String Teema(Context context){
        return context.getString(R.string.app_name) + "u " + aruandenimi + " - " + minueesnimi +
                " " + minuperenimi + ", " + opilaseinstrument + " " + aruandeperioodinimi;
    }

    public String KoostaKoond(Context context){
        String koond = "";
        koond = koond + "Soovituslik päevane harjutamise aeg : " +
                Tooriistad.KujundaHarjutusteMinutid(getPaevasharjutada()) +
                ReaVahetus;

        koond = koond + "Soovituslik harjutamise aeg perioodil: " +
                Tooriistad.KujundaHarjutusteMinutid(getPerioodipikkus() * getPaevasharjutada()) +
                ReaVahetus;

        Date now = new Date();
        Date algus = Tooriistad.MoodustaKuuAlgusKuupaev(now);
        Date lopp = Tooriistad.MoodustaKuuLopuKuupaev(now);
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(context);
        koond = koond + "Tegelik harjutamise aeg perioodil: " +
                Tooriistad.KujundaHarjutusteMinutid(mPPManager.ArvutaPerioodiMinutid(algus, lopp)) +
                ReaVahetus + ReaVahetus + ReaVahetus;

        List<String> pList = mPPManager.HarjutusKordadeStatistikaPerioodis(algus, lopp);
        for ( String teoserida : pList){
            koond = koond + teoserida + ReaVahetus;
        }

        return koond;
    }

    public String KoostaDetailneSisu(){
        return "";
    }

    public String KoostaLopp(){
        return "";
    }

    public String AruandeKoguTekst(Context context){
        return KoostaKoond(context) + KoostaDetailneSisu() + KoostaLopp();
    }

    public int getPaevasharjutada() {
        return paevasharjutada;
    }
}
