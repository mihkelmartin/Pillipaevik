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
public class HeliFailDraiviTeenus extends IntentService {

    private HarjutusKord harjutusKord;

    public HeliFailDraiviTeenus() {
        super("HeliFailDraiviTeenus");
    }
    @Override

    protected void onHandleIntent(Intent workIntent)  {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        PilliPaevikDatabase mPP = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPP.getTeos(workIntent.getIntExtra("teosid",0));
        HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getApplicationContext());
        harjutusKord = harjutuskorradmap.get(workIntent.getIntExtra("harjutusid",0));
        if(BuildConfig.DEBUG) Log.d("HeliFailDraiviTeenus","Harjutuskord  :" + harjutusKord.toString());

        GoogleDriveUhendus mGD = GoogleDriveUhendus.getInstance();
        GoogleDriveUhendus.setActivity(null);
        DriveId mHDI = null;
        int writemode = DriveFile.MODE_WRITE_ONLY;
        if(harjutusKord.getHelifailidriveid() == null || harjutusKord.getHelifailidriveid().isEmpty()) {
            mHDI = mGD.LooDriveHeliFail(harjutusKord.getHelifail());
            harjutusKord.setHelifailidriveid(mGD.AnnaDriveID(mHDI));
            harjutusKord.setHelifailidriveidmuutumatu(mGD.AnnaDriveIDMuutumatu(mHDI));
            mPP.SalvestaHarjutusKord(getApplicationContext(), harjutusKord);
            if(BuildConfig.DEBUG) Log.d("HeliFailDraiviTeenus","Uus fail loodud");
        } else {
            mHDI = DriveId.decodeFromString(harjutusKord.getHelifailidriveid());
            // TODO Tegelikult READ_WRITE kui oskaks lisada
            writemode = DriveFile.MODE_WRITE_ONLY;
            if(BuildConfig.DEBUG) Log.d("HeliFailDraiviTeenus","Lisame olemasolevale failile");
        }
        DriveContents mFD = mGD.AvaDriveFail(mHDI, writemode);

        try {
            FileOutputStream out = new FileOutputStream(mFD.getParcelFileDescriptor().getFileDescriptor());
            FileInputStream in  = new FileInputStream(getFilesDir().getPath() + "/" + harjutusKord.getHelifail());
            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            mGD.SalvestaDrivei(mFD);
            Tooriistad.KustutaKohalikFail(getFilesDir(), harjutusKord.getHelifail());
        } catch (IOException e) {
            if(BuildConfig.DEBUG) Log.e("HeliFailDraiviTeenus","Lugemise/Kirjutamise viga :" + e.toString());
        }
    }

    public void setHarjutusKord(HarjutusKord harjutusKord) {
        this.harjutusKord = harjutusKord;
    }

}
