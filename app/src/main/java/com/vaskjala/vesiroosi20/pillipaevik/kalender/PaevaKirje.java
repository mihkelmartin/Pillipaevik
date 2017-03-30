package com.vaskjala.vesiroosi20.pillipaevik.kalender;


import java.util.Date;
import java.util.List;

/**
 * Created by mihkel on 12.06.2016.
 */
public class PaevaKirje extends KalendriKirje {


    public Date kuupaev;
    public int kordadearv;
    public int pikkussekundites;
    public boolean bHarjutusedAvatud = false;
    public boolean bAndmebaasistLaetud = false;
    public List<HarjutuskordKirje> Harjutused;


    public PaevaKirje(Tyyp tyyp, String tiitel, Date kuupaev, int kordadearv, int pikkussekundites){
        super(tyyp, tiitel);
        this.kuupaev = kuupaev;
        this.kordadearv = kordadearv;
        this.pikkussekundites = pikkussekundites;
    }
}