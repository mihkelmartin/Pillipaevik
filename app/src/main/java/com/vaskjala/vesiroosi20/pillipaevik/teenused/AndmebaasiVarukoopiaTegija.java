package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;

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
