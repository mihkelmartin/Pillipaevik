package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.util.Log;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;

import java.io.IOException;

/**
 * Created by mihkel on 13.06.2016.
 */
public class GoogleDriveTagasiSide extends DriveEventService {

    @Override
    public void onCompletion(CompletionEvent event) {

        if (event.getStatus() == CompletionEvent.STATUS_SUCCESS) {
            if(BuildConfig.DEBUG) Log.d("GoogleDriveTagasiSide", "Õnnestus: " + event.getStatus());
            if(event.getDriveId() != null){
                GoogleDriveUhendus mGDU = new GoogleDriveUhendus(getApplicationContext(), null);
                if(mGDU.LooDriveUhendusAsunkroonselt()) {
                    if (BuildConfig.DEBUG) Log.d("GoogleDriveTagasiSide", "Drive id: " + event.getDriveId());
                    mGDU.DriveFailAvalikuks(event.getDriveId());
                    String retVal = mGDU.AnnaWebLink(event.getDriveId());
                    if (retVal != null) {
                        PilliPaevikDatabase mPP = new PilliPaevikDatabase(getApplicationContext());
                        mPP.SalvestaHarjutuskorraWebLink(event.getDriveId().toInvariantString(), retVal);
                    }
                }
                mGDU.KatkestaDriveUhendus();
            } else {
                if(BuildConfig.DEBUG) Log.e("GoogleDriveTagasiSide", "DriveId==null");
            }
        } else {
            if (BuildConfig.DEBUG) Log.e("GoogleDriveTagasiSide", "Ebaõnnestumine: " + event.getStatus());
        }
        event.dismiss();
    }
}
