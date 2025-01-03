package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.HarjutusKord;
import com.vaskjala.vesiroosi20.pillipaevik.R;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Created by mihkel on 7.06.2016.
 */
public class GoogleDriveUhendus  {

    private static final String[] SCOPES = { DriveScopes.DRIVE_METADATA };

    private Context mApplicationContext = null;
    private Activity mAktiivneActivity = null;

    private Drive mGoogleDrive = null;
    private GoogleAccountCredential mCredential = null;
    private String mPilliPaevikKaustDriveId = null;
    private com.google.api.services.drive.Drive mService = null;

    public GoogleDriveUhendus(Context applicationcontext, Activity activity) {
        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Konstruktor");
        this.mApplicationContext = applicationcontext;
        mAktiivneActivity = activity;
    }

    public void LooDriveUhendus() {

        if(Tooriistad.KasGoogleKontoOlemas(mApplicationContext)) {
            String googlekonto = Tooriistad.AnnaGoogleKonto(mApplicationContext);
            if (mGoogleDrive == null) {
                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(
                                mApplicationContext, Collections.singleton(DriveScopes.DRIVE_FILE));
                credential.setSelectedAccountName(googlekonto);
                // Initialize HttpTransport and JsonFactory
                HttpTransport httpTransport = new NetHttpTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                mGoogleDrive = new Drive.Builder(
                        httpTransport,
                        jsonFactory,
                        credential)
                        .setApplicationName("Pillipäevik")
                        .build()
                ;
            }
        } else {
            Tooriistad.SeadistaGoogleDriveOlekSeadeteFailis(mApplicationContext, false);
            if(Tooriistad.isGooglePlayServicesAvailable(mAktiivneActivity)) {
                mAktiivneActivity.startActivityForResult(AccountPicker.newChooseAccountIntent(null,
                        null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true,
                        mApplicationContext.getString(R.string.konto_valimise_pealkiri), null, null, null),
                        Tooriistad.PEAMINE_KONTO_VALIMINE);
            } else {
                Tooriistad.NaitaHoiatust(mAktiivneActivity, mApplicationContext.getString(R.string.google_play_teenused_puuduvad_vea_pealkiri),
                        mApplicationContext.getString(R.string.konto_valimise_vea_tekst));
            }
        }
    }
    public boolean LooDriveUhendusAsunkroonselt() {

        // Kas saab nii ja mis siis kui googlekonto = ""
        boolean retVal = false;
        if(Tooriistad.KasGoogleKontoOlemas(mApplicationContext)) {
            LooDriveUhendus();
            if (mGoogleDrive != null) {
                if (BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "LooDriveUhendusAsunkroonselt õnnestus");
                String query = "mimeType = 'application/vnd.google-apps.folder' and name = '" +
                        PilliPaevikDatabase.DATABASE_NAME + "'";
                // Otsi Pillipaeviku Google Drive Kaust
                FileList result = null;
                try {
                    result = mGoogleDrive.files().list()
                            .setQ(query)
                            .setSpaces("drive")
                            .setFields("files(id, name)")
                            .execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mPilliPaevikKaustDriveId = result.getFiles().get(0).getId();

                if (mPilliPaevikKaustDriveId == null) {
                    File fileMetadata = new File();
                    fileMetadata.setName(PilliPaevikDatabase.DATABASE_NAME);
                    fileMetadata.setMimeType("application/vnd.google-apps.folder");
                    File folder;
                    try {
                        folder = mGoogleDrive.files().create(fileMetadata)
                                .setFields("id")
                                .execute();
                        if (BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", PilliPaevikDatabase.DATABASE_NAME + " loodi");
                        mPilliPaevikKaustDriveId = folder.getId();
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", PilliPaevikDatabase.DATABASE_NAME + " loomisel viga");
                    }
                }
            } else {
                if (BuildConfig.DEBUG)
                    Log.e("GoogleDriveUhendus", "Ühendus puudub");
            }
        }
        return retVal;
    }
    public void KatkestaDriveUhendus() {
        if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "KatkestaDriveUhendus");
        mGoogleDrive = null;
    }

    private String PilliPaevikKaustaDriveId() {
        return mPilliPaevikKaustDriveId;
    }

    public void SeadistaDriveRestUhendus(boolean besimenekord){

        if(mCredential == null)
            mCredential = GoogleAccountCredential.usingOAuth2(mApplicationContext, Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());

        if((Tooriistad.isGooglePlayServicesAvailable(mAktiivneActivity))) {
            if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Play teenused olemas");
            if((mCredential.getSelectedAccountName() != null)){
                if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Konto olemas");
                if( isDeviceOnline() ){
                    if(BuildConfig.DEBUG) Log.d("GoogleDriveUhendus", "Oleme internetis. Alustan eelautoriseerimisega.");
                    HttpTransport httpTransport = new NetHttpTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                    mService = new com.google.api.services.drive.Drive.Builder(
                            httpTransport, jsonFactory, mCredential)
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
                chooseAccount(besimenekord);
            }
        }
        else {
            if(BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Play teenused puuduvad");
            acquireGooglePlayServices();
        }

    }

    public String LooDriveHeliFail(String name) {
        File retVal = null;

        if (mGoogleDrive != null && mPilliPaevikKaustDriveId != null) {
            File metadata = new File()
                    .setParents(Collections.singletonList(mPilliPaevikKaustDriveId))
                    .setName(name)
                    .setMimeType("audio/mp4");
            // Create a file in the root folder
            try {
                retVal = mGoogleDrive.files().create(metadata).execute();
            } catch (IOException e) {
                if(BuildConfig.DEBUG) Log.e("LooDriveHeliFail", "Viga faili loomisel" + e.getMessage());
            }

        } else {
            if(BuildConfig.DEBUG) Log.e("LooDriveHeliFail", "Drive ühendus puudus või Pillipaevik kaust DriveId puudus");
        }
        return retVal.getId();
    }

    public String AnnaWebLink(String driveId) {
        String retVal = null;
        try {
            retVal = mGoogleDrive.files().get(driveId).execute().getWebViewLink();
        } catch (IOException e) {
            if(BuildConfig.DEBUG) Log.d("AnnaWebLink", e.getMessage());
        }
        return retVal;
    }

    public boolean SalvestaDrivei(HarjutusKord harjutusKord, java.io.File heliFail) {

        boolean retVal = false;
        if (mGoogleDrive != null) {
            FileContent mediaContent = new FileContent("audio/mp4", heliFail);
            try {
                File metadata = new File()
                        .setParents(Collections.singletonList(mPilliPaevikKaustDriveId))
                        .setName(harjutusKord.getHelifail())
                        .setMimeType("audio/mp4");
                metadata.setStarred(true);
                metadata.setViewedByMe(true);
                metadata.setViewedByMeTime(new DateTime(System.currentTimeMillis()));

                mGoogleDrive.files().update(harjutusKord.getHelifailidriveid(), metadata, mediaContent);
                if(BuildConfig.DEBUG) Log.d("SalvestaDrivei", "Drive-i salvestamine õnnestus");
                retVal = true;
            } catch (IOException e) {
                if(BuildConfig.DEBUG) Log.e("SalvestaDrivei", "Drive-i salvestamine ebaõnnestus:" + e.getMessage());
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("SalvestaDrivei", "Viga faili salvestamisel. Drive ühendus puudub");
        }
        return retVal;
    }
    public FileInputStream AvaDriveFail(HarjutusKord harjutusKord) throws FileNotFoundException {

        java.io.File helifail = new java.io.File(mAktiivneActivity.getFilesDir().getPath() + "/" + harjutusKord.getHelifail());
        FileInputStream in = new FileInputStream(helifail);
        if (mGoogleDrive != null && harjutusKord.getHelifailidriveid() != null ) {
            try {
                FileOutputStream outputStream = new FileOutputStream(helifail);
                mGoogleDrive.files().get(harjutusKord.getHelifailidriveid()).executeMediaAndDownloadTo(outputStream);

            } catch (IOException e) {
                if(BuildConfig.DEBUG) Log.e("AvaDriveFail", "Viga faili Draivist laadimisel");
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("AvaDriveFail", "Viga faili avamisel. Drive ühendus puudub või driveId==null");
        }
        return in;
    }
    public void DriveFailAvalikuks(String driveId){

        com.google.api.services.drive.Drive mService;
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        if(isDeviceOnline()) {
            if (mCredential == null) {
                mCredential = GoogleAccountCredential.usingOAuth2(
                        mApplicationContext, Arrays.asList(SCOPES))
                        .setBackOff(new ExponentialBackOff());
                SeadistaKontoSeadetest();
            }

            mService = new com.google.api.services.drive.Drive.Builder(
                    httpTransport, jsonFactory, mCredential)
                    .setApplicationName("PilliPaevik")
                    .build();

            com.google.api.services.drive.model.Permission avalikluba =
                    new com.google.api.services.drive.model.Permission().setType("anyone").setRole("reader");

            if(driveId != null) {
                try {
                    mService.permissions().create(driveId, avalikluba).execute();
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

        if(mGoogleDrive != null) {
            if (failiDriveID != null && !failiDriveID.isEmpty()) {
                try {
                    mGoogleDrive.files().delete(failiDriveID).execute();
                    if (BuildConfig.DEBUG) Log.d("KustutuaDraivist", "Kustutatud " + failiDriveID);
                } catch (IOException e) {
                    if(BuildConfig.DEBUG) Log.e("Kusutatamise AsyncTask", "Ei suuda kustutada: " + e.getMessage());
                }
            } else {
                if(BuildConfig.DEBUG) Log.e("KustutaDriveFail", "DriveId null või tühi");
            }
        } else {
            if(BuildConfig.DEBUG) Log.e("KustutaDriveFail", "Drive ühendus puudub");
        }

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
    private void chooseAccount(boolean besimenekord) {
        if(SeadistaKontoSeadetest()){
            SeadistaDriveRestUhendus(besimenekord);
        } else {
            if(besimenekord) {
                if (BuildConfig.DEBUG) Log.e("GoogleDriveUhendus", "Konto puudub, kuvame valikuakna");
                mAktiivneActivity.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        Tooriistad.GOOGLE_DRIVE_REST_KONTO_VALIMINE);
            } else {
                if (BuildConfig.DEBUG) Log.e("chooseAccount", "Kontot ei õnnestunud sättida. Õigused puuduvad või mõni muu viga?");
                Tooriistad.NaitaHoiatust((Activity) mAktiivneActivity,
                        mApplicationContext.getString(R.string.google_drive_uhenduse_vea_pealkiri),
                        mApplicationContext.getString(R.string.google_drive_uhenduse_vea_tekst));
            }
        }
    }
    private boolean SeadistaKontoSeadetest(){
        boolean retVal = false;
        if(Tooriistad.KasGoogleKontoOlemas(mApplicationContext)) {
            String googlekonto = Tooriistad.AnnaGoogleKonto(mApplicationContext);
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
