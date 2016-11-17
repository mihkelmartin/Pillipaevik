package com.vaskjala.vesiroosi20.pillipaevik;

/**
 * Created by mihkel on 1.09.2016.
 */
public interface TeosFragmendiKuulaja {
    void AlustaHarjutust(int teosid);
    void LisaTehtudHarjutus(int teosid);
    void HarjutusValitud(int teosid, int harjutusud);
    void SeaTeosid(int teosid);
    void KustutaTeos(Teos teos, int itemposition);
    void MuudaTeos(Teos teos);
}
