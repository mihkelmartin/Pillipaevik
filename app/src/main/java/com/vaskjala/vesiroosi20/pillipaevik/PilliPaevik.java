package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Application;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

/**
 * Created by mihkel on 31.05.2016.
 */
public class PilliPaevik extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Tooriistad.exportDB(getApplicationContext());
        Tooriistad.importDB(getApplicationContext());
        PilliPaevikDatabase.setContext(getApplicationContext());
    }
}
