package com.vaskjala.vesiroosi20.pillipaevik;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.Tooriistad;

/**
 * Created by mihkel on 31.05.2016.
 */
public class PilliPaevik extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Tooriistad.exportDB(getApplicationContext());
            Tooriistad.importDB(getApplicationContext());
        }
    }
}
