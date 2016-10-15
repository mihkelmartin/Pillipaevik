package com.vaskjala.vesiroosi20.pillipaevik;

/**
 * Created by mihkel on 1.09.2016.
 */
public interface HarjutusFragmendiKuulaja {
    void HarjutusLisatud(int teosid, int harjutusid);
    void HarjutusKustutatud(int teosid, int harjutusid, int itemposition, int kustutamisealge);
    void HarjutusMuudetud(int teosid, int harjutusid, int itemposition);
    void SeaHarjutusid(int harjutuseid);
}
