package com.vaskjala.vesiroosi20.pillipaevik;

/**
 * Created by mihkel on 1.09.2016.
 */
public interface TeosFragmendiKuulaja {
    void HarjutusValitud(int teosid, int harjutusud);
    void KustutaTeos(int teosid, int itemposition);
    void AlustaHarjutust(int teosid);
    void LisaTehtudHarjutus(int teosid);
    void VarskendaTeosList();
    void VarskendaTeosListiElement(int position);
}
