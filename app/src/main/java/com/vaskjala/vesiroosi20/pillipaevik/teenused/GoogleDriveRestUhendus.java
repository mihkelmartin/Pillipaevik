package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.Arrays;

/**
 * Created by mihkel on 14.06.2016.
 */
public class GoogleDriveRestUhendus {

    private static final String[] SCOPES = { DriveScopes.DRIVE_METADATA };

    private static GoogleAccountCredential mCredential = null;
    private static Activity mDriveActivity = null;

    // Vaid 1 instance
    private static GoogleDriveRestUhendus instance = new GoogleDriveRestUhendus();
    private GoogleDriveRestUhendus() {}
    public static GoogleDriveRestUhendus getInstance(){
        return instance;
    }

    public static GoogleAccountCredential GoogleApiCredential(){
        return mCredential;
    }

    public static void setmDriveActivity(Activity mDriveActivity) {
        GoogleDriveRestUhendus.mDriveActivity = mDriveActivity;
    }


    public boolean Uhendu(){
        boolean retVal = false;

        if(mCredential == null)
            mCredential = GoogleAccountCredential.usingOAuth2(
                    mDriveActivity , Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());

        if((retVal =  isGooglePlayServicesAvailable())) {
            Log.d("GoogleDriveRestUhendus", "Play teenused olemas");
            if((retVal = (mCredential.getSelectedAccountName() != null))){
                Log.d("GoogleDriveRestUhendus", "Konto olemas");
                if((retVal = isDeviceOnline())){
                    Log.d("GoogleDriveRestUhendus", "Oleme internetis. Kõik kombes ühendus olemas.");

                } else {
                    Log.e("GoogleDriveRestUhendus", "Internet puudub");
                }
            } else {
                Log.e("GoogleDriveRestUhendus", "Konto valimata");
                chooseAccount();
            }
        }
        else
            Log.e("GoogleDriveRestUhendus", "Play teenused puuduvad");

        return retVal;
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mDriveActivity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mDriveActivity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                mDriveActivity,
                connectionStatusCode,
                // See on jälle ActivityREsultdi good
                1010);
        dialog.show();
    }

    private void chooseAccount() {
        String googledrivekonto =
                mDriveActivity.getSharedPreferences(mDriveActivity.getString(R.string.seadete_fail), Context.MODE_PRIVATE)
                .getString("googledrivekonto", null);
        if(googledrivekonto != null){
            mCredential.setSelectedAccountName(googledrivekonto);
            Uhendu();
        } else {
            // Start a dialog from which the user can choose an account
            mDriveActivity.startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    1001);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mDriveActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
