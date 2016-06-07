package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.*;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileDescriptor;
import java.util.Date;

/**
 * Created by mihkel on 7.06.2016.
 */
public class GoogleDriveUhendus  implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{


    //create an object of GoogleDriveUhendus
    private static GoogleDriveUhendus instance = new GoogleDriveUhendus();

    private static GoogleApiClient mGoogleApiClient = null;
    private static Activity mDriveActivity = null;
    private static DriveId mPilliPaevikKaust = null;

    //make the constructor private so that this class cannot be
    //instantiated
    private GoogleDriveUhendus(){}

    //Get the only object available
    public static GoogleDriveUhendus getInstance(){
        return instance;
    }
    public static GoogleApiClient GoogleApiKlient(){
        return mGoogleApiClient;
    }
    private static DriveId PilliPaevikKaustaDriveId(){
        return mPilliPaevikKaust;
    }

    public void LooDriveUhendus(Activity activity){

        mDriveActivity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    public void KatkestaUhnedus(){
        if(mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        Log.d("KatkestaUhnedus", "KatkestaUhnedus");
    }

    public DriveId LooDriveHeliFail(String name){
        DriveId retVal = null;
        GoogleApiClient mLocalGAC = GoogleApiKlient();
        DriveId mLocalDId = PilliPaevikKaustaDriveId();

        if(mLocalGAC != null && mLocalDId != null) {
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

    public String AnnaDriveID (DriveId driveId) {
        Log.d("AnnaDriveID", driveId.encodeToString());
        return  driveId.encodeToString();
    }
    public DriveId AnnaDriveID (String driveId) {
        Log.d("AnnaDriveID", driveId);
        return  DriveId.decodeFromString(driveId);
    }

    public String AnnaWebLink (DriveId driveId) {
        String retVal = "";
        GoogleApiClient mLocalGAC = GoogleApiKlient();

        if(mLocalGAC != null) {
            retVal = driveId.asDriveResource().getMetadata(mLocalGAC).await().getMetadata().getWebContentLink();
        }
        Log.d("AnnaWebLink", "WebLink");
        return retVal;
    }

    public void SalvestaDrivei(DriveContents muudetudsisu){

        GoogleApiClient mLocalGAC = GoogleApiKlient();
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setStarred(true)
                .setLastViewedByMeDate(new Date()).build();
        if (mLocalGAC != null) {
            muudetudsisu.commit(mLocalGAC, changeSet);
        }
    }

    public DriveContents AvaDriveFail(DriveId driveId, int mode) {
        DriveContents retVal = null;
        DriveFile file = driveId.asDriveFile();
        GoogleApiClient mLocalGAC = GoogleApiKlient();

        if (mLocalGAC != null) {
            retVal = file.open(mLocalGAC, mode, null).await().
                    getDriveContents();
        }

        return retVal;
    }


    private class DriveFailiTagasiside implements ResultCallback<DriveFolder.DriveFileResult> {

        private DriveId loodudFail = null;
        public DriveFailiTagasiside(){
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
    };

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                //TODO
                connectionResult.startResolutionForResult(mDriveActivity, 1000);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), mDriveActivity, 0).show();
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
                        Log.e("GoogleDriveUhendus", "count=" + count);
                        for (Metadata metadata : metadataBuffer) {
                            mPilliPaevikKaust = metadata.getDriveId();
                            Log.e("GoogleDriveUhendus", metadata.getTitle()+ " " + metadata.getDriveId() + " " +
                            metadata.getOriginalFilename() + " " + metadata.getMimeType());
                        }
                        if(count == 0) {
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(PilliPaevikDatabase.DATABASE_NAME).build();
                            pGDRoot.createFolder(mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);
                        }
                        metadataBuffer.release();

                    }
                });



    }

    ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Tooriistad", "Error while trying to create the folder");
                        return;
                    }
                    mPilliPaevikKaust = result.getDriveFolder().getDriveId();
                    Log.d("Tooriistad","Created a folder: " + result.getDriveFolder().getDriveId());
                }
            };

    @Override
    public void onConnectionSuspended(int i) {

    }

}
