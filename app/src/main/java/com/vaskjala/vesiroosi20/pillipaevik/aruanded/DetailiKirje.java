package com.vaskjala.vesiroosi20.pillipaevik.aruanded;

import java.util.Date;

/**
 * Created by mihkel on 26.06.2016.
 */
public class DetailiKirje {

    public DetailiKirje(String nimi, Date algusaeg, int pikkussekundites, String weblink, int weblinkaruandele){
        this.nimi = nimi;
        this.algusaeg = algusaeg;
        this.pikkussekundites = pikkussekundites;
        this.weblink = weblink;
        this.weblinkaruandele = weblinkaruandele;
    }

    private String nimi;
    private Date algusaeg;
    private int pikkussekundites;
    private String weblink;
    private int weblinkaruandele;

    public Date getAlgusaeg() {
        return algusaeg;
    }
    public String getNimi() {
        return nimi;
    }
    public int getPikkussekundites() {
        return pikkussekundites;
    }
    public String getWeblink() {
        return weblink;
    }
    public int getWeblinkaruandele() {
        return weblinkaruandele;
    }
}
