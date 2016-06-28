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

        GoogleDriveUhendus mGDU = GoogleDriveUhendus.getInstance();
        if (event.getStatus() == CompletionEvent.STATUS_SUCCESS) {
            if(BuildConfig.DEBUG) Log.d("GoogleDriveTagasiSide", "Õnnestus: " + event.getStatus());
            if(event.getDriveId() != null){
                if(BuildConfig.DEBUG) Log.d("GoogleDriveTagasiSide", "Drive id: " + event.getDriveId() + " Resource id:"
                        + event.getDriveId().getResourceId());

                com.google.api.services.drive.Drive mService = null;
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                mService = new com.google.api.services.drive.Drive.Builder(
                        transport, jsonFactory, GoogleDriveUhendus.GoogleApiCredential())
                        .setApplicationName("PilliPaevik")
                        .build();
                com.google.api.services.drive.model.Permission avalikluba =
                        new com.google.api.services.drive.model.Permission().setType("anyone").setRole("reader");
                try {
                    mService.permissions().create(event.getDriveId().getResourceId(), avalikluba).execute();
                }   catch (UserRecoverableAuthIOException e) {
                    if(BuildConfig.DEBUG) Log.e("GoogleDriveTagasiSide", "Catchisin hoopis " + e.toString());
                }
                catch (IOException e){
                    if(BuildConfig.DEBUG) Log.e("GoogleDriveTagasiSide", "Karm Eceptisioon" + e.toString());
                }
            } else {
                if(BuildConfig.DEBUG) Log.e("GoogleDriveTagasiSide", "Drive id on null");
            }
        } else {
            if (BuildConfig.DEBUG) Log.e("GoogleDriveTagasiSide", "Ebaõnnestumine");
        }

        String retVal = event.getDriveId().asDriveResource().getMetadata(GoogleDriveUhendus.GoogleApiKlient()).await().getMetadata().getAlternateLink();
        if(retVal != null) {
            PilliPaevikDatabase mPP = new PilliPaevikDatabase(getApplicationContext());
            mPP.SalvestaHarjutuskorraWebLink(event.getDriveId().toInvariantString(), retVal);
            if(BuildConfig.DEBUG) Log.d("GoogleDriveTagasiSide", "Faili link:" + retVal);
        } else {
            if (BuildConfig.DEBUG) Log.e("GoogleDriveTagasiSide", "Ei saanud WebLinki");
        }

        event.dismiss();
    }

}
