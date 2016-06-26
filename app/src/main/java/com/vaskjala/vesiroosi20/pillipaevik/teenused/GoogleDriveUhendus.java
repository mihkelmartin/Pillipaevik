package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.*;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.vaskjala.vesiroosi20.pillipaevik.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by mihkel on 7.06.2016.
 */
public class GoogleDriveUhendus  implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String[] SCOPES = { DriveScopes.DRIVE_METADATA };

    //create an object of GoogleDriveUhendus
    private static GoogleDriveUhendus instance = new GoogleDriveUhendus();

    private static GoogleApiClient mGoogleApiClient = null;
    private static GoogleAccountCredential mCredential = null;
    private static Activity mAktiivneActivity = null;
    private static DriveId mPilliPaevikKaust = null;
    private static com.google.api.services.drive.Drive mService = null;
    private static boolean bDriveRestApiValmis = false;

    //make the constructor private so that this class cannot be
    //instantiated
    private GoogleDriveUhendus() {
    }
    //Get the only object available
    public static GoogleDriveUhendus getInstance() {
        return instance;
    }

    public static void setActivity(Activity mDriveActivity) {
        GoogleDriveUhendus.mAktiivneActivity = mDriveActivity;
    }

    public void LooDriveUhendus() {

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mAktiivneActivity)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }
    public void KatkestaDriveUhendus() {
        Log.d("GoogleDriveUhendus", "KatkestaDriveUhendus");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public static GoogleApiClient GoogleApiKlient() {
        GoogleApiClient retVal = null;
        if(mGoogleApiClient != null)
            if(mGoogleApiClient.isConnected())
                retVal = mGoogleApiClient;

        return retVal;
    }
    private static DriveId PilliPaevikKaustaDriveId() {
        return mPilliPaevikKaust;
    }

    public void LooDriveRestUhendus(){

        if(mCredential == null)
            mCredential = GoogleAccountCredential.usingOAuth2(
                    mAktiivneActivity, Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());

        if((isGooglePlayServicesAvailable())) {
            Log.d("GoogleDriveUhendus", "Play teenused olemas");
            if((mCredential.getSelectedAccountName() != null)){
                Log.d("GoogleDriveUhendus", "Konto olemas");
                if( isDeviceOnline() ){
                    Log.d("GoogleDriveUhendus", "Oleme internetis. Kõik kombes ühendus olemas.");
                    Log.d("GoogleDriveUhendus", "Alustan eelautoriseerimisega");
                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    mService = new com.google.api.services.drive.Drive.Builder(
                            transport, jsonFactory, GoogleApiCredential())
                            .setApplicationName("PilliPaevik")
                            .build();
                    TeeEelAutoriseering mTeeAutoriseering = new TeeEelAutoriseering();
                    mTeeAutoriseering.execute();
                    Log.d("GoogleDriveUhendus", "Drive ühenduse REST loomine läbi");
                    bDriveRestApiValmis = true;
                } else {
                    Log.e("GoogleDriveUhendus", "Võrguühendus puudub");
                }
            } else {
                Log.e("GoogleDriveUhendus", "Konto valimata");
                chooseAccount();
            }
        }
        else {
            Log.e("GoogleDriveUhendus", "Play teenused puuduvad");
            acquireGooglePlayServices();
        }

    }

    public static GoogleAccountCredential GoogleApiCredential(){
        GoogleAccountCredential retVal = null;
        if(mCredential != null)
            if(bDriveRestApiValmis)
                retVal = mCredential;

        return retVal;
    }


    public DriveId LooDriveHeliFail(String name) {
        DriveId retVal = null;
        GoogleApiClient mGAC = GoogleApiKlient();
        DriveId mPPDId = PilliPaevikKaustaDriveId();

        if (mGAC != null && mPPDId != null) {
            DriveFolder PPfolder = mPPDId.asDriveFolder();
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(name)
                    .setMimeType("audio/mp4").build();
            // Create a file in the root folder
            DriveFolder.DriveFileResult fileResult = PPfolder.createFile(mGAC, changeSet, null).await();
            if(fileResult.getStatus().isSuccess()){
                retVal = fileResult.getDriveFile().getDriveId();
                Log.d("LooDriveHeliFail", "Fail loodud: " + retVal.toString());
            } else {
                Log.e("LooDriveHeliFail", "Viga faili loomisel" + fileResult.getStatus().getStatusMessage());
            }
        } else {
            Log.e("LooDriveHeliFail", "Viga faili loomisel. Drive ühendus puudus või Pillipaevik kaust DriveId puudus");
        }

        return retVal;
    }
    public DriveId AnnaDriveID(String driveId){
        DriveId dID = null;
        try {
            dID = DriveId.decodeFromString(driveId);
        } catch (IllegalArgumentException e){
            Log.e("AnnaDriveID", "Sobimatu DriveID!");
        }
        return dID;
    }
    public String AnnaDriveID(DriveId driveId) {
        String retVal = driveId.encodeToString();
        Log.d("AnnaDriveID", retVal);
        return retVal;
    }
    public String AnnaDriveIDMuutumatu(DriveId driveId) {
        String retVal = driveId.toInvariantString();
        Log.d("AnnaDriveIDMuutumatu", retVal);
        return retVal;
    }
    public String AnnaWebLink(DriveId driveId) {
        String retVal = "";
        GoogleApiClient mLocalGAC = GoogleApiKlient();

        if (mLocalGAC != null) {
            retVal = driveId.asDriveResource().getMetadata(mLocalGAC).await().getMetadata().getAlternateLink();
        }
        Log.d("AnnaWebLink", "WebLink");
        return retVal;
    }
    public void SalvestaDrivei(DriveContents muudetudsisu) {

        GoogleApiClient mGAC = GoogleApiKlient();
        if (mGAC != null) {
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setStarred(true)
                .setLastViewedByMeDate(new Date()).build();
            ExecutionOptions executionOptions = new ExecutionOptions.Builder()
                    .setNotifyOnCompletion(true)
                    .build();
            Status result = muudetudsisu.commit(mGAC, changeSet, executionOptions).await();
            if(result.getStatus().isSuccess()){
                Log.d("SalvestaDrivei", "Drive-i salvestamine õnnestus");
            }else {
                Log.e("SalvestaDrivei", "Drive-i salvestamine ebaõnnestus:" + result.getStatusMessage());
            }
        } else {
            Log.e("SalvestaDrivei", "Viga faili salvestamisel. Drive ühendus puudub");
        }

    }
    public DriveContents AvaDriveFail(DriveId driveId, int mode) {
        DriveContents retVal = null;
        DriveFile file = driveId.asDriveFile();
        GoogleApiClient mGAC = GoogleApiKlient();

        if (mGAC != null) {
            DriveApi.DriveContentsResult mDCR = file.open(mGAC, mode, null).await();
            if(mDCR.getStatus().isSuccess()) {
                retVal = mDCR.getDriveContents();
                Log.d("HeliFailDraiviTeenus", "Drive faili sisu avatud !" + retVal.toString());
            }
            else {
                Log.e("HeliFailDraiviTeenus", "Drive faili ei avatud: " + mDCR.getStatus().getStatusMessage());
            }
        }
        return retVal;
    }
    public void KustutaDriveFail(String failiDriveID) {
        KustutuaDraivisFailAsyncTask mKDFA = new KustutuaDraivisFailAsyncTask();
        mKDFA.driveId = failiDriveID;
        mKDFA.execute();
    }
    private class KustutuaDraivisFailAsyncTask extends AsyncTask<Void, Void, Void> {
        public String driveId = "";
        @Override
        protected Void doInBackground(Void... params) {
            GoogleApiClient mGAC = GoogleApiKlient();
            if(mGAC != null) {
                if (!driveId.isEmpty()) {
                    DriveId fileId = AnnaDriveID(driveId);
                    if (fileId != null) {
                        DriveFile fail = fileId.asDriveFile();
                        com.google.android.gms.common.api.Status deleteStatus =
                                fail.delete(mGAC).await();
                        if (!deleteStatus.isSuccess()) {
                            Log.e("Kusutatamise AsyncTask", "Ei suuda kustutada: " + deleteStatus.getStatusMessage());
                            return null;
                        }
                        // Remove stored DriveId.
                        Log.d("KustutuaDraivist", "Kustutatud " + driveId);
                    }
                }
            } else {
                Log.e("Kusutatamise AsyncTask", "Drive ühendus puudub");
            }
            return null;
        }
    }


    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GoogleDriveUhendus", "onConnectionFailed: " + connectionResult.toString());
        if (connectionResult.hasResolution()) {
            try {
                if (mAktiivneActivity != null) {
                    Log.e("GoogleDriveUhendus", "onConnectionFailed avame loa andmise akent");
                    connectionResult.startResolutionForResult(mAktiivneActivity, Tooriistad.GOOGLE_DRIVE_KONTO_VALIMINE);
                }
                else
                    Log.e("GoogleDriveUhendus", "onConnectionFailed on lahendus kuid meil ei ole vaadet mille seda näidata");

            } catch (IntentSender.SendIntentException e) {
                Log.e("GoogleDriveUhendus", "onConnectionFailed. Lahendus on, kuid ei suuda lahendada:" + e.toString());
            }
        } else {
            if (mAktiivneActivity != null)
                Tooriistad.NaitaHoiatust(mAktiivneActivity, "onConnectionFailed. Google Drive ühenduse viga", "Veakood :" + connectionResult.getErrorCode());
            else
                Log.e("GoogleDriveUhendus", "onConnectionFailed lahendust ei ole, veakood :" + connectionResult.getErrorCode());
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        final DriveFolder pGDRoot = Drive.DriveApi.getRootFolder(mGoogleApiClient);
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, PilliPaevikDatabase.DATABASE_NAME))
                .build();

        pGDRoot.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        // Iterate over the matching Metadata instances in mdResultSet
                        MetadataBuffer metadataBuffer = result.getMetadataBuffer();
                        int count = metadataBuffer.getCount();
                        Log.d("GoogleDriveUhendus", "Leitud " + PilliPaevikDatabase.DATABASE_NAME + " kaustade arv:" + count);
                        for (Metadata metadata : metadataBuffer) {
                            if (!metadata.isTrashed()) {
                                mPilliPaevikKaust = metadata.getDriveId();
                            }
                            Log.d("GoogleDriveUhendus", metadata.getTitle() + " " + metadata.getDriveId() + " " + metadata.isTrashed());
                        }
                        if (count == 0) {
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(PilliPaevikDatabase.DATABASE_NAME).build();
                            pGDRoot.createFolder(mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);
                        }
                        metadataBuffer.release();

                        Log.d("GoogleDriveUhendus", "Alusta Drive REST ühenduse loomisega");
                        LooDriveRestUhendus();
                    }
                });


    }

    ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("GoogleDriveUhendus", "Error while trying to create the folder" +
                                result.getStatus().getStatusMessage());
                        return;
                    }
                    mPilliPaevikKaust = result.getDriveFolder().getDriveId();
                    Log.d("GoogleDriveUhendus", "Created a folder: " + result.getDriveFolder().getDriveId());
                }
            };

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("GoogleDriveUhendus", "onConnectionSuspended: " + i);
    }


    private class TeeEelAutoriseering extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                com.google.api.services.drive.model.FileList request = mService.files().list().execute();
            } catch (UserRecoverableAuthIOException e) {
                mAktiivneActivity.startActivityForResult(e.getIntent(), Tooriistad.GOOGLE_DRIVE_REST_UHENDUSE_LUBA);
                Log.e("GoogleDriveUhendus", "TeeEelAutoriseering " + e.toString());
            } catch (IOException e) {
                Log.e("GoogleDriveUhendus", "TeeEelAutoriseering" + e.toString());
            }
            return null;
        }
    }


    // Ühenduse võimalikkuse testimine
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mAktiivneActivity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mAktiivneActivity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                mAktiivneActivity,
                connectionStatusCode,
                // See on jälle ActivityResulti kood
                1010);
        dialog.show();
    }
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mAktiivneActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    private void chooseAccount() {
        String googlekonto =
                mAktiivneActivity.getSharedPreferences(mAktiivneActivity.getString(R.string.seadete_fail), Context.MODE_PRIVATE)
                        .getString("googlekonto", null);
        if(googlekonto != null && !googlekonto.isEmpty()){
            Log.d("GoogleDriveUhendus", "Konto võetud seadetest:" + googlekonto);
            mCredential.setSelectedAccountName(googlekonto);
            LooDriveRestUhendus();
        } else {
            Log.e("GoogleDriveUhendus", "Konto puudub, kuvame valikuakna");
            // Start a dialog from which the user can choose an account
            mAktiivneActivity.startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    Tooriistad.GOOGLE_DRIVE_REST_KONTO_VALIMINE);
        }
    }
}