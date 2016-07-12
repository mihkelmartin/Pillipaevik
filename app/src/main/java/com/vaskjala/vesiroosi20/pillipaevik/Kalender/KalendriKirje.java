package com.vaskjala.vesiroosi20.pillipaevik.kalender;

import java.util.Date;
import java.util.List;

/**
 * Created by mihkel on 12.06.2016.
 */
public class KalendriKirje {

    public enum Tyyp {KUU, PAEV, HARJUTUS}

    private Tyyp tyyp;
    private String tiitel;
    // Kalendripäeva andmed


    public KalendriKirje(Tyyp tyyp, String tiitel){
        this.tyyp = tyyp;
        this.tiitel = tiitel;
    }

    public String getTiitel() {
        return tiitel;
    }

    public boolean KasKuu(){
        return tyyp == Tyyp.KUU;
    }
    public boolean KasPaev(){
        return tyyp == Tyyp.PAEV;
    }
    public boolean KasHarjutus(){
        return tyyp == Tyyp.HARJUTUS;
    }
}