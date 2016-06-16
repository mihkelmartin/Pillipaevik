package com.vaskjala.vesiroosi20.pillipaevik.aruanded;



import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mihkel on 5.06.2016.
 */


public class Aruanne {

    private final static String ReaVahetus = System.getProperty("line.separator");
    public String aruandekogutekst;

    private String aruandenimi;
    private String aruandeperioodinimi;
    private Date perioodialgus;
    private Date perioodilopp;
    private int perioodipikkus;

    private final String minueesnimi;
    private final String minuperenimi;
    private final String opilaseinstrument;
    private final String opetajaeesnimi;
    private final String opetajaperenimi;
    private final String opetajaepost;
    private final int paevasharjutada;

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

    private int getPerioodipikkus() {
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

    private String KoostaKoond(Context context){
        String koond = "<html><body>";
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
        koond = koond + "<b>Tegelik harjutamise aeg perioodil: </b>" +
                Tooriistad.KujundaHarjutusteMinutid(mPPManager.ArvutaPerioodiMinutid(algus, lopp)) +
                ReaVahetus + ReaVahetus + ReaVahetus;

        List<String> pList = mPPManager.HarjutusKordadeStatistikaPerioodis(algus, lopp);
        koond = koond + "<table>";
        for ( String teoserida : pList){
            koond = koond + teoserida + ReaVahetus;
        }
        koond = koond + "</table></body></html>";
        Log.d("Aruanne",koond);
        return koond;
    }

    private String KoostaDetailneSisu(){
        return "";
    }

    private String KoostaLopp(){
        return "";
    }

    public String AruandeKoguTekst(Context context){
        return KoostaKoond(context) + KoostaDetailneSisu() + KoostaLopp();
    }

    private int getPaevasharjutada() {
        return paevasharjutada;
    }
}
