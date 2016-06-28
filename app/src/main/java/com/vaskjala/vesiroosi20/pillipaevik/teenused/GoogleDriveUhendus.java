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
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
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
    private static final GoogleDriveUhendus instance = new GoogleDriveUhendus();

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

    public static void LooDriveUhendus() {

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mAktiivneActivity)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(getInstance())
                    .addOnConnectionFailedListener(getInstance())
                    .build();
        }
        mGoogleApiClient.connect();
    }
    public void KatkestaDriveUhendus() {
        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "KatkestaDriveUhendus");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public static GoogleApiClient GoogleApiKlient() {
        GoogleApiClient retVal = null;
        if(mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                retVal = mGoogleApiClient;
            } else {
                if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "mGoogleApiClient.isConnected() == false");
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Anna GoogleApiKlient. mGoogleApiClient == null");
        }

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
            if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Play teenused olemas");
            if((mCredential.getSelectedAccountName() != null)){
                if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Konto olemas");
                if( isDeviceOnline() ){
                    if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Oleme internetis. Kõik kombes ühendus olemas.");
                    if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Alustan eelautoriseerimisega");
                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    mService = new com.google.api.services.drive.Drive.Builder(
                            transport, jsonFactory, GoogleApiCredential())
                            .setApplicationName("PilliPaevik")
                            .build();
                    TeeEelAutoriseering mTeeAutoriseering = new TeeEelAutoriseering();
                    mTeeAutoriseering.execute();
                    if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Drive ühenduse REST loomine läbi");
                    bDriveRestApiValmis = true;
                } else {
                    if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Võrguühendus puudub");
                }
            } else {
                if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Konto valimata");
                chooseAccount();
            }
        }
        else {
            if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Play teenused puuduvad");
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
                if(BuildConfig.DEBUG) Log.d("LooDriveHeliFail", "Fail loodud: " + retVal.toString());
            } else {
                if(BuildConfig.DEBUG) Log.e("LooDriveHeliFail", "Viga faili loomisel" + fileResult.getStatus().getStatusMessage());
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("LooDriveHeliFail", "Viga faili loomisel. Drive ühendus puudus või Pillipaevik kaust DriveId puudus");
        }

        return retVal;
    }
    public DriveId AnnaDriveID(String driveId){
        DriveId dID = null;
        try {
            dID = DriveId.decodeFromString(driveId);
        } catch (IllegalArgumentException e){
            if(BuildConfig.DEBUG) Log.e("AnnaDriveID", "Sobimatu DriveID!");
        }
        return dID;
    }
    public String AnnaDriveID(DriveId driveId) {
        String retVal = driveId.encodeToString();
        if(BuildConfig.DEBUG) Log.d("AnnaDriveID", retVal);
        return retVal;
    }
    public String AnnaDriveIDMuutumatu(DriveId driveId) {
        String retVal = driveId.toInvariantString();
        if(BuildConfig.DEBUG) Log.d("AnnaDriveIDMuutumatu", retVal);
        return retVal;
    }
    public String AnnaWebLink(DriveId driveId) {
        String retVal = "";
        GoogleApiClient mLocalGAC = GoogleApiKlient();

        if (mLocalGAC != null) {
            retVal = driveId.asDriveResource().getMetadata(mLocalGAC).await().getMetadata().getAlternateLink();
        }
        if(BuildConfig.DEBUG) Log.d("AnnaWebLink", "WebLink");
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
                if(BuildConfig.DEBUG) Log.d("SalvestaDrivei", "Drive-i salvestamine õnnestus");
            } else {
                if(BuildConfig.DEBUG) Log.e("SalvestaDrivei", "Drive-i salvestamine ebaõnnestus:" + result.getStatusMessage());
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("SalvestaDrivei", "Viga faili salvestamisel. Drive ühendus puudub");
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
                if(BuildConfig.DEBUG) Log.d("HeliFailDraiviTeenus", "Drive faili sisu avatud !" + retVal.toString());
            }
            else {
                if(BuildConfig.DEBUG) Log.e("HeliFailDraiviTeenus", "Drive faili ei avatud: " + mDCR.getStatus().getStatusMessage());
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("AvaDriveFail", "Viga faili salvestamisel. Drive ühendus puudub");
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
                            if(BuildConfig.DEBUG) Log.e("Kusutatamise AsyncTask", "Ei suuda kustutada: " + deleteStatus.getStatusMessage());
                            return null;
                        }
                        if(BuildConfig.DEBUG) Log.d("KustutuaDraivist", "Kustutatud " + driveId);
                    }
                }
            } else {
                if(BuildConfig.DEBUG) Log.e("Kusutatamise AsyncTask", "Drive ühendus puudub");
            }
            return null;
        }
    }


    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "onConnectionFailed: " + connectionResult.toString());
        if (connectionResult.hasResolution()) {
            try {
                if (mAktiivneActivity != null) {
                    if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "onConnectionFailed avame loa andmise akent");
                    connectionResult.startResolutionForResult(mAktiivneActivity, Tooriistad.GOOGLE_DRIVE_KONTO_VALIMINE);
                }
                else {
                    if (BuildConfig.DEBUG)
                        Log.e("GoogleDriveUhendus", "onConnectionFailed on lahendus kuid meil ei ole vaadet mille seda näidata");
                }

            } catch (IntentSender.SendIntentException e) {
                if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "onConnectionFailed. Lahendus on, kuid ei suuda lahendada:" + e.toString());
            }
        } else {
            if (mAktiivneActivity != null) {
                Tooriistad.NaitaHoiatust(mAktiivneActivity, "onConnectionFailed. Google Drive ühenduse viga", "Veakood :" + connectionResult.getErrorCode());
            }
            else {
                if (BuildConfig.DEBUG)
                    Log.e("GoogleDriveUhendus", "onConnectionFailed lahendust ei ole, veakood :" + connectionResult.getErrorCode());
            }
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
                        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Leitud " + PilliPaevikDatabase.DATABASE_NAME + " kaustade arv:" + count);
                        for (Metadata metadata : metadataBuffer) {
                            if (!metadata.isTrashed()) {
                                mPilliPaevikKaust = metadata.getDriveId();
                            }
                            if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", metadata.getTitle() + " " + metadata.getDriveId() + " " + metadata.isTrashed());
                        }
                        if (count == 0) {
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(PilliPaevikDatabase.DATABASE_NAME).build();
                            pGDRoot.createFolder(mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);
                        }
                        metadataBuffer.release();

                        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Alusta Drive REST ühenduse loomisega");
                        LooDriveRestUhendus();
                    }
                });


    }

    private final ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Error while trying to create the folder" +
                                result.getStatus().getStatusMessage());
                        return;
                    }
                    mPilliPaevikKaust = result.getDriveFolder().getDriveId();
                    if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Created a folder: " + result.getDriveFolder().getDriveId());
                }
            };

    @Override
    public void onConnectionSuspended(int i) {
        if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "onConnectionSuspended: " + i);
    }


    private class TeeEelAutoriseering extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                com.google.api.services.drive.model.FileList request = mService.files().list().execute();
            } catch (UserRecoverableAuthIOException e) {
                mAktiivneActivity.startActivityForResult(e.getIntent(), Tooriistad.GOOGLE_DRIVE_REST_UHENDUSE_LUBA);
                if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "TeeEelAutoriseering " + e.toString());
            } catch (IOException e) {
                if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "TeeEelAutoriseering" + e.toString());
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
    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
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
            if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Konto võetud seadetest:" + googlekonto);
            mCredential.setSelectedAccountName(googlekonto);
            LooDriveRestUhendus();
        } else {
            if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Konto puudub, kuvame valikuakna");
            // Start a dialog from which the user can choose an account
            mAktiivneActivity.startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    Tooriistad.GOOGLE_DRIVE_REST_KONTO_VALIMINE);
        }
    }
}