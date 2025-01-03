package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
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


        PilliPaevikDatabase mPP = new PilliPaevikDatabase(getApplicationContext());
        Teos teos = mPP.getTeos(workIntent.getIntExtra("teosid",0));
        HashMap<Integer, HarjutusKord> harjutuskorradmap = teos.getHarjutuskorradmap(getApplicationContext());
        harjutusKord = harjutuskorradmap.get(workIntent.getIntExtra("harjutusid",0));
        if(BuildConfig.DEBUG) Log.d("LisaFailDraiviTeenus","Harjutuskord  :" + harjutusKord.toString());

        java.io.File heliFail = new File(getFilesDir().getPath() + "/" + harjutusKord.getHelifail());
        GoogleDriveUhendus mGD = new GoogleDriveUhendus(getApplicationContext(), null);
        if(mGD.LooDriveUhendusAsunkroonselt()) {
            String mHDI;
            if (harjutusKord.getHelifailidriveid() == null || harjutusKord.getHelifailidriveid().isEmpty()) {
                mHDI = mGD.LooDriveHeliFail(harjutusKord.getHelifail());
                if (mHDI != null) {
                    harjutusKord.setHelifailidriveid(mHDI);
                    harjutusKord.setHelifailidriveidmuutumatu(mHDI);
                    harjutusKord.Salvesta(getApplicationContext());
                    if (BuildConfig.DEBUG) Log.d("LisaFailDraiviTeenus", "Helifaili Drive andmed harjutusse salvestatud");
                } else {
                    if (BuildConfig.DEBUG) Log.e("LisaFailDraiviTeenus", "Helifaili Drive andmeid harjutusse ei salvestatud");
                }
            } else {
                mHDI = harjutusKord.getHelifailidriveid();
                if (BuildConfig.DEBUG) Log.d("LisaFailDraiviTeenus", "Helifail oli olemas. Kirjutame üle");
            }

            if (mGD.SalvestaDrivei(harjutusKord, heliFail)) {
               Tooriistad.KustutaKohalikFail(getFilesDir(), harjutusKord.getHelifail());
            }
        }
        mGD.KatkestaDriveUhendus();

    }
}
