package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.Activity;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.*;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by mihkel on 7.06.2016.
 */
public class GoogleDriveUhendus  implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {


    //create an object of GoogleDriveUhendus
    private static GoogleDriveUhendus instance = new GoogleDriveUhendus();

    private static GoogleApiClient mGoogleApiClient = null;
    private static Activity mDriveActivity = null;
    private static DriveId mPilliPaevikKaust = null;
    private static com.google.api.services.drive.Drive mService = null;

    //make the constructor private so that this class cannot be
    //instantiated
    private GoogleDriveUhendus() {
    }

    //Get the only object available
    public static GoogleDriveUhendus getInstance() {
        return instance;
    }

    public static GoogleApiClient GoogleApiKlient() {
        return mGoogleApiClient;
    }

    private static DriveId PilliPaevikKaustaDriveId() {
        return mPilliPaevikKaust;
    }

    public static void setmDriveActivity(Activity mDriveActivity) {
        GoogleDriveUhendus.mDriveActivity = mDriveActivity;
    }

    public void LooDriveUhendus(Activity activity) {

        mDriveActivity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    public void KatkestaUhnedus() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        Log.d("KatkestaUhnedus", "KatkestaUhnedus");
    }

    public DriveId LooDriveHeliFail(String name) {
        DriveId retVal = null;
        GoogleApiClient mLocalGAC = GoogleApiKlient();
        DriveId mLocalDId = PilliPaevikKaustaDriveId();

        if (mLocalGAC != null && mLocalDId != null) {
            DriveFolder PPfolder = mLocalDId.asDriveFolder();
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(name)
                    .setMimeType("audio/mp4").build();
            // Create a file in the root folder
            retVal = PPfolder.createFile(mLocalGAC, changeSet, null).await().getDriveFile().getDriveId();
        }
        Log.d("LooDriveHeliFail", "Fail loodud: " + retVal.toString());
        return retVal;
    }

    public String AnnaDriveID(DriveId driveId) {
        Log.d("AnnaDriveID", driveId.encodeToString());
        return driveId.encodeToString();
    }

    public DriveId AnnaDriveID(String driveId) {
        Log.d("AnnaDriveID", driveId);
        return DriveId.decodeFromString(driveId);
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

        GoogleApiClient mLocalGAC = GoogleApiKlient();
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setStarred(true)
                .setLastViewedByMeDate(new Date()).build();
        if (mLocalGAC != null) {
            ExecutionOptions executionOptions = new ExecutionOptions.Builder()
                    .setNotifyOnCompletion(true)
                    .build();
            muudetudsisu.commit(mLocalGAC, changeSet, executionOptions);
        }
    }

    public DriveContents AvaDriveFail(DriveId driveId, int mode) {
        DriveContents retVal = null;
        DriveFile file = driveId.asDriveFile();
        GoogleApiClient mLocalGAC = GoogleApiKlient();

        if (mLocalGAC != null) {
            retVal = file.open(mLocalGAC, mode, null).await().getDriveContents();
            Log.d("HeliFailDraiviTeenus", "Drive faili sisu avatud !" + retVal.toString());
        }

        return retVal;
    }

    public void KustutaDriveFail(String failiDriveID) {
        KustutuaDraivisFailAsyncTask mKDFA = new KustutuaDraivisFailAsyncTask();
        mKDFA.driveId = failiDriveID;
        mKDFA.execute();
    }


    private class DriveFailiTagasiside implements ResultCallback<DriveFolder.DriveFileResult> {

        private DriveId loodudFail = null;

        public DriveFailiTagasiside() {
            super();
        }

        public DriveId getLoodudFail() {
            return loodudFail;
        }

        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.e("GoogledDriveYhendus", "Problem while trying to create a folder");
                return;
            }
            loodudFail = result.getDriveFile().getDriveId();
            Log.d("GoogledDriveYhendus", "Fail loodud");
        }
    }

    ;

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GoogleDriveUhendus", "onConnectionFailed: " + connectionResult.toString());
        if (connectionResult.hasResolution()) {
            try {
                if (mDriveActivity != null)
                    connectionResult.startResolutionForResult(mDriveActivity, 1000);
                else
                    Log.e("GoogleDriveUhendus", "onConnectionFailed on lahendus kuid meil ei ole vaadet mille seda näidata");

            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            if (mDriveActivity != null)
                Tooriistad.NaitaHoiatust(mDriveActivity, "Google Drive ühenduse viga", "Veakood :" + connectionResult.getErrorCode());
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
                        String DriveResourceId = "";
                        Log.e("GoogleDriveUhendus", "Leitud " + PilliPaevikDatabase.DATABASE_NAME + " kaustade arv:" + count);
                        for (Metadata metadata : metadataBuffer) {
                            if (!metadata.isTrashed()) {
                                mPilliPaevikKaust = metadata.getDriveId();
                                DriveResourceId = metadata.getDriveId().getResourceId();
                            }
                            Log.e("GoogleDriveUhendus", metadata.getTitle() + " " + metadata.getDriveId() + " " + metadata.isTrashed());
                        }
                        if (count == 0) {
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(PilliPaevikDatabase.DATABASE_NAME).build();
                            pGDRoot.createFolder(mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);
                        }
                        metadataBuffer.release();

                        GoogleDriveRestUhendus mGDRU = GoogleDriveRestUhendus.getInstance();
                        Log.d("GoogleDriveUhendus", "Alusta Drive REST ühenduse loomisega");
                        mGDRU.setmDriveActivity(mDriveActivity);
                        mGDRU.Uhendu();

                        HttpTransport transport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                        mService = new com.google.api.services.drive.Drive.Builder(
                                transport, jsonFactory, mGDRU.GoogleApiCredential())
                                .setApplicationName("PilliPaevik")
                                .build();
                        TeeEelAutoriseering mTeeAutoriseering = new TeeEelAutoriseering();
                        mTeeAutoriseering.execute();
                        Log.d("GoogleDriveUhendus", "Drive ühenduse REST loomine läbi");


                    }
                });


    }

    ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("GoogleDriveUhendus", "Error while trying to create the folder");
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


    private class KustutuaDraivisFailAsyncTask extends AsyncTask<Void, Void, Void> {

        public String driveId = "";

        @Override
        protected Void doInBackground(Void... params) {
            if (!driveId.isEmpty()) {
                DriveId fileId = DriveId.decodeFromString(driveId);
                DriveFile fail = fileId.asDriveFile();
                // Call to delete app data file. Consider using DriveResource.trash()
                // for user visible files.
                com.google.android.gms.common.api.Status deleteStatus =
                        fail.delete(mGoogleApiClient).await();
                if (!deleteStatus.isSuccess()) {
                    Log.e("Kusutatamise AsyncTask", "Ei suuda kustutada: " + driveId);
                    return null;
                }
                // Remove stored DriveId.
                Log.d("KustutuaDraivist", "Kustutatud " + driveId);
            }
            return null;
        }
    }

    private class TeeEelAutoriseering extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                com.google.api.services.drive.model.FileList request = mService.files().list().execute();
                List<com.google.api.services.drive.model.File> mF = request.getFiles();
                for(com.google.api.services.drive.model.File mfile : mF){
                    Log.d("TeeEelAutoriseerin", "Listin faile" + mfile.getName());
                }
            } catch (UserRecoverableAuthIOException e) {
                mDriveActivity.startActivityForResult(e.getIntent(), 1004);
                Log.e("GoogleDriveTagasiSide", "Catchisin hoopis " + e.toString());
            } catch (IOException e) {
                Log.e("GoogleDriveTagasiSide", "Karm Eceptisioon" + e.toString());
            }
            return null;
        }
    }
}