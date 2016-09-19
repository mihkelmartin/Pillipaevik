package com.vaskjala.vesiroosi20.pillipaevik.kalender;

import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKord;

import java.util.Date;
import java.util.List;

/**
 * Created by mihkel on 12.06.2016.
 */
public class HarjutuskordKirje extends KalendriKirje {

    PaevaKirje vanem;
    HarjutusKord harjutusKord;

    public HarjutuskordKirje(Tyyp tyyp, String tiitel, HarjutusKord harjutusKord, PaevaKirje paevaKirje){
        super(tyyp, tiitel);
        vanem = paevaKirje;
        this.harjutusKord = harjutusKord;
    }
}