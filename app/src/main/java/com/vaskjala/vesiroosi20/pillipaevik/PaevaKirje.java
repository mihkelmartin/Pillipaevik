package com.vaskjala.vesiroosi20.pillipaevik;

import java.util.Date;
import java.util.List;

/**
 * Created by mihkel on 12.06.2016.
 */
public class PaevaKirje {
    public Date kuupaev;
    public int kordadearv;
    public int pikkussekundites;
    public boolean bPeaKirje = true;
    public boolean bHarjutusedAvatud = false;
    public boolean bAndmebaasistLaetud = false;
    public List<PaevaKirje> Harjutused;

    public String Teos;
    public int harjutusepikkus;
    public String DriveId;

    public PaevaKirje(Date d, int ka, int ps){
        kuupaev = d;
        kordadearv = ka;
        pikkussekundites = ps;
    }
}