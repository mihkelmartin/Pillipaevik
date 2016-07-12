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

import java.io.*;
import java.util.HashMap;

/**
 * Created by mihkel on 7.06.2016.
 */
public class LisaFailDraiviTeenus extends IntentService {

    private HarjutusKord harjutusKord;

    public LisaFailDraiviTeenus() {
        super("LisaFailDraiviTeenus");
    }
    @Override

    protected void onHandleIntent(Intent workIntent)  {

        try {
            PilliPaevikDatabase mPP = new PilliPaevikDatabase(getApplicationContext());
            Teos teos = mPP.getTeos(workIntent.getIntExtra("teosid",0));
            HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getApplicationContext());
            harjutusKord = harjutuskorradmap.get(workIntent.getIntExtra("harjutusid",0));
            if(BuildConfig.DEBUG) Log.d("LisaFailDraiviTeenus","Harjutuskord  :" + harjutusKord.toString());

            FileInputStream in = new FileInputStream(getFilesDir().getPath() + "/" + harjutusKord.getHelifail());
            GoogleDriveUhendus mGD = new GoogleDriveUhendus(getApplicationContext(), null);
            if(mGD.LooDriveUhendusAsunkroonselt()) {
                DriveId mHDI;
                DriveContents mFD;
                int writemode = DriveFile.MODE_WRITE_ONLY;

                if (harjutusKord.getHelifailidriveid() == null || harjutusKord.getHelifailidriveid().isEmpty()) {
                    mHDI = mGD.LooDriveHeliFail(harjutusKord.getHelifail());
                    if (mHDI != null) {
                        harjutusKord.setHelifailidriveid(mGD.AnnaDriveID(mHDI));
                        harjutusKord.setHelifailidriveidmuutumatu(mGD.AnnaDriveIDMuutumatu(mHDI));
                        harjutusKord.Salvesta(getApplicationContext());
                        if (BuildConfig.DEBUG) Log.d("LisaFailDraiviTeenus", "Helifaili Drive andmed harjutusse salvestatud");
                    } else {
                        if (BuildConfig.DEBUG) Log.e("LisaFailDraiviTeenus", "Helifaili Drive andmeid harjutusse ei salvestatud");
                    }
                } else {
                    mHDI = mGD.AnnaDriveID(harjutusKord.getHelifailidriveid());
                    if (BuildConfig.DEBUG) Log.d("LisaFailDraiviTeenus", "Helifail oli olemas. Kirjutame üle");
                }

                mFD = mGD.AvaDriveFail(mHDI, writemode);
                if (mFD != null) {
                    try {
                        FileOutputStream out = new FileOutputStream(mFD.getParcelFileDescriptor().getFileDescriptor());
                        byte[] buf = new byte[2048];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        if (mGD.SalvestaDrivei(mFD)) {
                            Tooriistad.KustutaKohalikFail(getFilesDir(), harjutusKord.getHelifail());
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e("LisaFailDraiviTeenus", "Lugemise/kirjutamise viga :" + e.toString());
                    }
                }
            }
            mGD.KatkestaDriveUhendus();
        } catch (FileNotFoundException e) {
            if (BuildConfig.DEBUG) Log.e("LisaFailDraiviTeenus", "Lugemise/kirjutamise viga :" + e.toString());
        }
    }
}
