package com.vaskjala.vesiroosi20.pillipaevik;

import android.util.Log;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;

/**
 * Created by mihkel on 13.06.2016.
 */
public class GoogleDriveTagasiSide extends DriveEventService {

    @Override
    public void onCompletion(CompletionEvent event) {

        if (event.getStatus() == CompletionEvent.STATUS_SUCCESS) {
            Log.d("GoogleDriveTagasiSide", "Õnnestus: " + event.getStatus());
            if(event.getDriveId() != null){
                Log.d("GoogleDriveTagasiSide", "Drive id: " + event.getDriveId() + " Resource id:"
                        + event.getDriveId().getResourceId());

            } else {
                Log.e("GoogleDriveTagasiSide", "Drive id on null");
            }
        } else
            Log.e("GoogleDriveTagasiSide", "Ebaõnnestumine");

        event.dismiss();
    }

}
