package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.AndmebaasiVarukoopiaTegija;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.KorrastaDraivFailidTeenus;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

import java.util.Locale;

/**
 * Created by mihkel on 31.05.2016.
 */
public class PilliPaevik extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, AndmebaasiVarukoopiaTegija.class);
        startService(intent);
        Tooriistad.importDB(getApplicationContext());
    }
}
