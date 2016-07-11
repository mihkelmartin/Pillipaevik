package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.GoogleDriveUhendus;

public class TeaveActivity extends AppCompatActivity {

    private TextView googlelitsents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teave);
        Toolbar toolbar = (Toolbar) findViewById(R.id.teave_toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(R.string.teave_tiitel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        googlelitsents = ((TextView)findViewById(R.id.googlelitsents));
    }

    @Override
    protected void onStart() {
        super.onStart();

        googlelitsents.setText(getString(R.string.teave_laen));
        LoeGoogleLitsentAsync mLGL = new LoeGoogleLitsentAsync();
        mLGL.execute();
    }

    private class LoeGoogleLitsentAsync extends AsyncTask<Void, Void, Void> {

        private String googlelitsentsstr;
        protected Void doInBackground(Void ... params) {
            googlelitsentsstr = GoogleApiAvailability
                        .getInstance()
                        .getOpenSourceSoftwareLicenseInfo(getApplicationContext());
            return null;
        }

        protected void onPostExecute(Void result) {

            googlelitsents.setText(googlelitsentsstr);
            super.onPostExecute(result);
        }
    }
}
