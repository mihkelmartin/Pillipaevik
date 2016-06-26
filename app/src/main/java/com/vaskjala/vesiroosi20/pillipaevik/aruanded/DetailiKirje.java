package com.vaskjala.vesiroosi20.pillipaevik.aruanded;

import java.util.Date;

/**
 * Created by mihkel on 26.06.2016.
 */
public class DetailiKirje {

    public DetailiKirje(String nimi, Date algusaeg, int pikkussekundites, String weblink){
        this.nimi = nimi;
        this.algusaeg = algusaeg;
        this.pikkussekundites = pikkussekundites;
        this.weblink = weblink;
    }

    private String nimi;
    private Date algusaeg;
    private int pikkussekundites;
    private String weblink;

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
}
