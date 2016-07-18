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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by mihkel on 7.06.2016.
 */
public class AndmebaasiVarukoopiaTegija extends IntentService {


    public AndmebaasiVarukoopiaTegija() {
        super("AndmebaasiVarukoopiaTegija");
    }
    @Override

    protected void onHandleIntent(Intent workIntent)  {
        if(BuildConfig.DEBUG) Log.d("AndmebaasiVarukoopia...", "Kutsun Tooriistad.exportDB");
        Tooriistad.exportDB(getApplicationContext());
    }
}
