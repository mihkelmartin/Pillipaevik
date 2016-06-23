package com.vaskjala.vesiroosi20.pillipaevik.aruanded;

import android.content.Context;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Date;

/**
 * Created by mihkel on 5.06.2016.
 */
public class Kuuaruanne extends Aruanne {

    public final static String ReaVahetus = System.getProperty("line.separator");

    public Kuuaruanne(Context context){

        super(context, "kuuaruanne");
        Date now = new Date();
        setPerioodialgus(Tooriistad.MoodustaKuuAlgusKuupaev(now));
        setPerioodilopp(Tooriistad.MoodustaKuuLopuKuupaev(now));

    }


}
