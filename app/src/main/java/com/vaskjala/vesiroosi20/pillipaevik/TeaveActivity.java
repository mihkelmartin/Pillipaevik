package com.vaskjala.vesiroosi20.pillipaevik;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.google.android.gms.common.GoogleApiAvailability;

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
            googlelitsentsstr = "";
            return null;
        }

        protected void onPostExecute(Void result) {

            googlelitsents.setText(googlelitsentsstr);
            super.onPostExecute(result);
        }
    }
}
