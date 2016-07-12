package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKord;
import com.vaskjala.vesiroosi20.pillipaevik.Teos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by mihkel on 7.06.2016.
 */
public class KustutaFailDraivistTeenus extends IntentService {

    public KustutaFailDraivistTeenus() {
        super("KustutaFailDraivistTeenus");
    }
    @Override

    protected void onHandleIntent(Intent workIntent)  {
        String driveid = workIntent.getStringExtra("driveid");
        if(driveid != null && !driveid.isEmpty()) {
            GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getApplicationContext(), null);
            if(mGDU.LooDriveUhendusAsunkroonselt()) {
                mGDU.KustutaDriveFail(driveid);
            }
            mGDU.KatkestaDriveUhendus();
        } else {
            if (BuildConfig.DEBUG) Log.e("KustutaFailDraivistTeen", "driveid null või tühi");
        }
    }
}
