package com.vaskjala.vesiroosi20.pillipaevik.aruanded;

import android.content.Context;
import android.content.SharedPreferences;
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

    private Context context;
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

        this.context = context;
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
    private int getPaevasharjutada() {
        return paevasharjutada;
    }

    public String Teema(){
        return context.getString(R.string.rakenduse_pealkiri) + " " + aruandenimi + " - " + minueesnimi +
                " " + minuperenimi + ", " + opilaseinstrument + ", " + aruandeperioodinimi;
    }
    private String KoostaKoond(){
        String koond = context.getString(R.string.aruanne_koondi_pealkiri) + ReaVahetus + ReaVahetus;
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(context);

        if(getPaevasharjutada() > 0) {
            koond = koond + context.getString(R.string.aruanne_soovituslik_harjutamise_aeg) +
                    Tooriistad.KujundaHarjutusteMinutid(context.getApplicationContext(), getPaevasharjutada()) +
                    ReaVahetus;

            koond = koond + context.getString(R.string.aruanne_soovituslik_harjutamise_aeg_kokku) +
                    Tooriistad.KujundaHarjutusteMinutid(context.getApplicationContext(), getPerioodipikkus() * getPaevasharjutada()) +
                    ReaVahetus;

            koond = koond + context.getString(R.string.aruanne_tegelik_harjutamise_aeg_kokku) +
                    Tooriistad.KujundaHarjutusteMinutid(context.getApplicationContext(),
                            mPPManager.ArvutaPerioodiMinutid(getPerioodialgus(), getPerioodilopp())) +
                    ReaVahetus + ReaVahetus;
        }

        List<String> pList = mPPManager.HarjutusKordadeStatistikaPerioodis(getPerioodialgus(), getPerioodilopp());
        for ( String teoserida : pList){
            koond = koond + teoserida + ReaVahetus;
        }
        koond = koond + ReaVahetus;
        return koond;
    }
    private String KoostaDetailneSisu(){
        String detail = "";
        String formaat = "%-30s%s";
        PilliPaevikDatabase mPPManager = new PilliPaevikDatabase(context);
        List<DetailiKirje> pList =  mPPManager.HarjutusKorradPerioodis(getPerioodialgus(),getPerioodilopp());

        for ( DetailiKirje teoserida : pList){
            if(teoserida.getWeblink() != null && !teoserida.getWeblink().isEmpty() &&
                    teoserida.getWeblinkaruandele() == 1){
                String kuupaev = Tooriistad.KujundaKuupaevSonaline(teoserida.getAlgusaeg());
                detail = detail + String.format(formaat,teoserida.getNimi(),kuupaev) + " " +
                        teoserida.getWeblink() +
                        ReaVahetus;
            }
        }
        if(!detail.isEmpty())
            detail = context.getString(R.string.aruanne_salvestatud_harjutused) + ReaVahetus + ReaVahetus + detail + ReaVahetus + ReaVahetus +
                    context.getString(R.string.aruanne_paevade_kaupa) + ReaVahetus;

        String kuupaeveelmine = "";
        for ( DetailiKirje teoserida : pList){
            String kuupaev = Tooriistad.KujundaKuupaevSonaline(teoserida.getAlgusaeg());
            if(!kuupaev.equalsIgnoreCase(kuupaeveelmine)){
                detail = detail + ReaVahetus + kuupaev + ReaVahetus;
                kuupaeveelmine = kuupaev;
            }
            detail = detail + String
                    .format(formaat,teoserida.getNimi(),
                        Tooriistad.KujundaHarjutusteMinutid(context.getApplicationContext(),
                                Tooriistad.ArvutaMinutidUmardaUles(teoserida.getPikkussekundites())))
                    + ReaVahetus;
        }
        return detail + ReaVahetus;
    }
    private String KoostaLopp(){
        return context.getString(R.string.aruanne_tervitades) + ReaVahetus +
                minueesnimi + " " + minuperenimi + context.getString(R.string.aruanne_pillipaeviku_vahendusel);
    }

    public String AruandeKoguTekst(){
        return KoostaKoond() + KoostaDetailneSisu() + KoostaLopp();
    }


}
