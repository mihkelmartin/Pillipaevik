package com.vaskjala.vesiroosi20.pillipaevik;

import java.util.Date;

/**
 * Created by mihkel on 12.06.2016.
 */
public class PaevaKirje {
    public Date kuupaev;
    public int kordadearv;
    public int pikkussekundites;

    public PaevaKirje(Date d, int ka, int ps){
        kuupaev = d;
        kordadearv = ka;
        pikkussekundites = ps;
    }
}