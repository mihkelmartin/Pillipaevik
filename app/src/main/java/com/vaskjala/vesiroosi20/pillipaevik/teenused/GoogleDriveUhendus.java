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

    private Context mApplicationContext = null;
    private Activity mAktiivneActivity = null;

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleAccountCredential mCredential = null;
    private DriveId mPilliPaevikKaust = null;
    private com.google.api.services.drive.Drive mService = null;

    public GoogleDriveUhendus(Context applicationcontext, Activity activity) {
        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Konstruktor");
        this.mApplicationContext = applicationcontext;
        mAktiivneActivity = activity;
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
    public boolean LooDriveUhendusAsunkroonselt() {

        boolean retVal = false;
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mApplicationContext)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .build();
        }
        ConnectionResult mCR = mGoogleApiClient.blockingConnect();
        if(mCR.isSuccess()){
            if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "LooDriveUhendusAsunkroonselt õnnestus");

            // Otsi Pillipaeviku Google Drive Kaust
            final DriveFolder pGDRoot = Drive.DriveApi.getRootFolder(mGoogleApiClient);
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, PilliPaevikDatabase.DATABASE_NAME))
                    .build();
            DriveApi.MetadataBufferResult result = pGDRoot.queryChildren(mGoogleApiClient, query).await();
            if(result.getStatus().isSuccess()) {
                MetadataBuffer metadataBuffer = result.getMetadataBuffer();
                int count = metadataBuffer.getCount();
                if (BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Leitud " + PilliPaevikDatabase.DATABASE_NAME + " kaustade arv:" + count);
                for (Metadata metadata : metadataBuffer) {
                    if (!metadata.isTrashed()) {
                        if (BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "LooDriveUhendusAsunkroonselt. Kaust leitud: " + metadata.getTitle());
                        mPilliPaevikKaust = metadata.getDriveId();
                        retVal = true;
                        break;
                    }
                }
                metadataBuffer.release();
            } else {
                if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", PilliPaevikDatabase.DATABASE_NAME + " kausta päring ei õnnestunud");
            }
            if(mPilliPaevikKaust == null) {
                if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", PilliPaevikDatabase.DATABASE_NAME + " kausta ei leitud");
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "LooDriveUhendusAsunkroonselt ei õnnestunud:" + mCR.getErrorMessage());
        }
        return retVal;
    }
    public void KatkestaDriveUhendus() {
        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "KatkestaDriveUhendus");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    private DriveId PilliPaevikKaustaDriveId() {
        return mPilliPaevikKaust;
    }

    public void SeadistaDriveRestUhendus(){

        if(mCredential == null)
            mCredential = GoogleAccountCredential.usingOAuth2(mApplicationContext, Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());

        if((isGooglePlayServicesAvailable())) {
            if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Play teenused olemas");
            if((mCredential.getSelectedAccountName() != null)){
                if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Konto olemas");
                if( isDeviceOnline() ){
                    if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Oleme internetis. Alustan eelautoriseerimisega.");
                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    mService = new com.google.api.services.drive.Drive.Builder(
                            transport, jsonFactory, mCredential)
                            .setApplicationName("PilliPaevik")
                            .build();
                    TeeEelAutoriseering mTeeAutoriseering = new TeeEelAutoriseering();
                    mTeeAutoriseering.execute();
                    if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Drive ühenduse REST loomine läbi");
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

    public DriveId LooDriveHeliFail(String name) {
        DriveId retVal = null;
        DriveId mPPDId = PilliPaevikKaustaDriveId();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mPPDId != null) {
            DriveFolder PPfolder = mPPDId.asDriveFolder();
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(name)
                    .setMimeType("audio/mp4").build();
            // Create a file in the root folder
            DriveFolder.DriveFileResult fileResult = PPfolder.createFile(mGoogleApiClient, changeSet, null).await();
            if(fileResult.getStatus().isSuccess()){
                retVal = fileResult.getDriveFile().getDriveId();
                if(BuildConfig.DEBUG) Log.d("LooDriveHeliFail", "Fail loodud: " + retVal.toString());
            } else {
                if(BuildConfig.DEBUG) Log.e("LooDriveHeliFail", "Viga faili loomisel" + fileResult.getStatus().getStatusMessage());
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("LooDriveHeliFail", "Drive ühendus puudus või Pillipaevik kaust DriveId puudus");
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
        String retVal = null;

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            retVal = driveId.asDriveResource().getMetadata(mGoogleApiClient).await().getMetadata().getAlternateLink();
            if(retVal == null){
                if(BuildConfig.DEBUG) Log.e("AnnaWebLink", "WebLinki ei saadud");
            } else {
                if(BuildConfig.DEBUG) Log.d("AnnaWebLink", "WebLink: " + retVal);
            }
        } else{
            if(BuildConfig.DEBUG) Log.e("AnnaWebLink", "Drive ühendus puudus");
        }
        return retVal;
    }
    public boolean SalvestaDrivei(DriveContents muudetudsisu) {

        boolean retVal = false;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setStarred(true)
                .setLastViewedByMeDate(new Date()).build();
            ExecutionOptions executionOptions = new ExecutionOptions.Builder()
                    .setNotifyOnCompletion(true)
                    .build();
            Status result = muudetudsisu.commit(mGoogleApiClient, changeSet, executionOptions).await();
            if(result.getStatus().isSuccess()){
                if(BuildConfig.DEBUG) Log.d("SalvestaDrivei", "Drive-i salvestamine õnnestus");
                retVal = true;
            } else {
                if(BuildConfig.DEBUG) Log.e("SalvestaDrivei", "Drive-i salvestamine ebaõnnestus:" + result.getStatusMessage());
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("SalvestaDrivei", "Viga faili salvestamisel. Drive ühendus puudub");
        }
        return retVal;
    }
    public DriveContents AvaDriveFail(DriveId driveId, int mode) {

        DriveContents retVal = null;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && driveId != null ) {
            DriveFile file = driveId.asDriveFile();
            DriveApi.DriveContentsResult mDCR = file.open(mGoogleApiClient, mode, null).await();
            if(mDCR.getStatus().isSuccess()) {
                retVal = mDCR.getDriveContents();
                if(BuildConfig.DEBUG) Log.d("LisaFailDraiviTeenus", "Drive faili sisu avatud !" + retVal.toString());
            }
            else {
                if(BuildConfig.DEBUG) Log.e("LisaFailDraiviTeenus", "Drive faili ei avatud: " + mDCR.getStatus().getStatusMessage());
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("AvaDriveFail", "Viga faili salvestamisel. Drive ühendus puudub või driveId==null");
        }
        return retVal;
    }
    public void DriveFailAvalikuks(DriveId driveId){

        com.google.api.services.drive.Drive mService;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        if(isDeviceOnline()) {
            if (mCredential == null) {
                mCredential = GoogleAccountCredential.usingOAuth2(
                        mApplicationContext, Arrays.asList(SCOPES))
                        .setBackOff(new ExponentialBackOff());
                SeadistaKontoSeadetest();
            }

            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("PilliPaevik")
                    .build();

            com.google.api.services.drive.model.Permission avalikluba =
                    new com.google.api.services.drive.model.Permission().setType("anyone").setRole("reader");

            String driveIdStr = driveId.getResourceId();
            if(driveIdStr != null) {
                try {
                    mService.permissions().create(driveIdStr, avalikluba).execute();
                } catch (UserRecoverableAuthIOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e("GoogleDriveUhendus", "DriveFailAvalikuks. UserRecoverableAuthIOException:" + e.toString());
                } catch (IOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e("GoogleDriveUhendus", "DriveFailAvalikuks. IOException:" + e.toString());
                }
            } else {
                Log.e("GoogleDriveUhendus", "DriveFailAvalikuks. getResourceId == null");
            }
        } else {
            Log.e("GoogleDriveUhendus", "DriveFailAvalikuks. Internetiühendus puudub ");
        }
    }
    public void KustutaDriveFail(String failiDriveID) {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            if (failiDriveID != null && !failiDriveID.isEmpty()) {
                DriveId fileId = AnnaDriveID(failiDriveID);
                if (fileId != null) {
                    DriveFile fail = fileId.asDriveFile();
                    com.google.android.gms.common.api.Status deleteStatus =
                            fail.delete(mGoogleApiClient).await();
                    if (deleteStatus.isSuccess()) {
                        if (BuildConfig.DEBUG) Log.d("KustutuaDraivist", "Kustutatud " + failiDriveID);
                    } else {
                        if(BuildConfig.DEBUG) Log.e("Kusutatamise AsyncTask", "Ei suuda kustutada: " + deleteStatus.getStatusMessage());
                    }
                }
            } else {
                if(BuildConfig.DEBUG) Log.e("KustutaDriveFail", "DriveId null või tühi");
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("KustutaDriveFail", "Drive ühendus puudub");
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
    public void onConnected(@Nullable Bundle bundle) {

        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "onConnected algus");
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
                        KatkestaDriveUhendus();

                        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Alusta Drive REST ühenduse loomisega");
                        SeadistaDriveRestUhendus();
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
                Tooriistad.GOOGLE_PLAY_TEENUSTE_VEAAKEN);
        dialog.show();
    }
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    private void chooseAccount() {
        if(SeadistaKontoSeadetest()){
            SeadistaDriveRestUhendus();
        } else {
            if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Konto puudub, kuvame valikuakna");
            mAktiivneActivity.startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    Tooriistad.GOOGLE_DRIVE_REST_KONTO_VALIMINE);
        }
    }
    private boolean SeadistaKontoSeadetest(){
        boolean retVal = false;
        String googlekonto = mApplicationContext
                .getSharedPreferences(mApplicationContext.getString(R.string.seadete_fail), Context.MODE_PRIVATE)
                .getString("googlekonto", null);
        if(googlekonto != null && !googlekonto.isEmpty()) {
            if (BuildConfig.DEBUG) Log.d("SeadistaKontoSeadetest", "Konto võetud seadetest:" + googlekonto);
            mCredential.setSelectedAccountName(googlekonto);
            if(mCredential.getSelectedAccount() != null) {
                retVal = true;
            } else {
                retVal = false;
                if (BuildConfig.DEBUG) Log.e("SeadistaKontoSeadetest", "Kontot ei õnnestunud sättida. Õigused puuduvad ?" + googlekonto);
            }
        }
        return retVal;
    }
}
