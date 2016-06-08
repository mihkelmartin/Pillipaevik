package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

        GoogleDriveUhendus mGD = GoogleDriveUhendus.getInstance();
        DriveId mHDI = mGD.LooDriveHeliFail(harjutusKord.getHelifail());
        harjutusKord.setHelifailidriveid(mGD.AnnaDriveID(mHDI));
        harjutusKord.setHelifailidriveweblink(mGD.AnnaWebLink(mHDI));
        mPP.SalvestaHarjutusKord(getApplicationContext(), harjutusKord);
        DriveContents mFD = mGD.AvaDriveFail(mHDI, DriveFile.MODE_WRITE_ONLY);

        // TODO Allolev tööriistadesse
        FileInputStream in = null;
        try {
            in = new FileInputStream(getFilesDir().getPath().toString() + "/" + harjutusKord.getHelifail());
        } catch (IOException e) {
            Log.e("HeliFailDraiviTeenus","Lugemise viga :" + e.toString());
        }

        FileOutputStream out = new FileOutputStream(mFD.getParcelFileDescriptor().getFileDescriptor());
        byte[] buf = new byte[2048];
        int len;
        try {
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            Log.e("HeliFailDraiviTeenus","Kirjutamise viga :" + e.toString());
        }
        mGD.SalvestaDrivei(mFD);

        // TODO Kui Draivi tegemine ei õnnestunud siis ei tohi kustutada
        // Ja siis tuleb hiljem draivi teha
        // TODO Allolev Tooriistadessse
        File dir = getFilesDir();
        File file = new File(dir, harjutusKord.getHelifail());
        if(file.delete())
            Log.d("HeliFailDraiviTeenus","Telefonis oleva fail kustutatud :" + getFilesDir().getPath().toString() + "/" + harjutusKord.getHelifail());
        else
            Log.e("HeliFailDraiviTeenus","Telefonis oleva faili kustutamise viga !");

    }

    public void setHarjutusKord(HarjutusKord harjutusKord) {
        this.harjutusKord = harjutusKord;
    }

}
