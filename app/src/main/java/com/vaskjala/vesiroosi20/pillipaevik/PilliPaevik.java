package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Application;

/**
 * Created by mihkel on 31.05.2016.
 */
public class PilliPaevik extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Tooriistad.exportDB(getApplicationContext());
        Tooriistad.importDB(getApplicationContext());
    }
}
