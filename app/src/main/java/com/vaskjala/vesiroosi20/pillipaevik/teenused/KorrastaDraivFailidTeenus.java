package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKord;
import java.util.List;

/**
 * Created by mihkel on 7.06.2016.
 */
public class KorrastaDraivFailidTeenus extends IntentService {

    public KorrastaDraivFailidTeenus() {
        super("KorrastaDraivFailidTeenus");
    }
    @Override

    protected void onHandleIntent(Intent workIntent)  {

        if(Tooriistad.kasKasutadaGoogleDrive(getApplicationContext())) {

            PilliPaevikDatabase mPP = new PilliPaevikDatabase(getApplicationContext());
            List<String> pListWebLink = mPP.LeiaDraivistIlmaLingitaFailid();
            if(!pListWebLink.isEmpty()) {
                GoogleDriveUhendus mGD = new GoogleDriveUhendus(getApplicationContext(), null);
                if (mGD.LooDriveUhendusAsunkroonselt()) {
                    for (String driveIdStr : pListWebLink) {
                        if (BuildConfig.DEBUG) Log.d("KorrastaDraivFailidTe..", "Weblingi tegemine Drive idle: " + driveIdStr);
                        mGD.DriveFailAvalikuks(driveIdStr);
                        String retVal = mGD.AnnaWebLink(driveIdStr);
                        if (retVal != null) {
                            mPP.SalvestaHarjutuskorraWebLink(driveIdStr, retVal);
                        }
                    }
                }
                mGD.KatkestaDriveUhendus();
            } else {
                if (BuildConfig.DEBUG) Log.d("KorrastaDraivFailidTe..", "Ei leitud ilma Weblingita faile");
            }

            List<HarjutusKord> pListFail = mPP.LeiaDraivistPuuduvadFailid();
            if(!pListFail.isEmpty()) {
                GoogleDriveUhendus mGD = new GoogleDriveUhendus(getApplicationContext(), null);
                if (mGD.LooDriveUhendusAsunkroonselt()) {
                    for (HarjutusKord harjutusKord : pListFail) {
                        if(BuildConfig.DEBUG) Log.d("KorrastaDraivFailidTe..", "Harjutuskord fail Draivi:"+ harjutusKord.toString());
                        Intent intent = new Intent(this, LisaFailDraiviTeenus.class);
                        intent.putExtra("teosid", harjutusKord.getTeoseid());
                        intent.putExtra("harjutusid", harjutusKord.getId());
                        startService(intent);
                    }
                }
                mGD.KatkestaDriveUhendus();
            } else {
                if (BuildConfig.DEBUG) Log.d("KorrastaDraivFailidTe..", "Ei leitud draivist puuduvaid faile");
            }
        } else {
            if (BuildConfig.DEBUG) Log.d("KorrastaDraivFailidTe..", "Google Draivi kasutus ei ole sisse lülitatud.");
        }
    }
}
