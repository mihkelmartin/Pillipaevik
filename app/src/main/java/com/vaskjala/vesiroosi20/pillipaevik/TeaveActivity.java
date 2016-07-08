package com.vaskjala.vesiroosi20.pillipaevik;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
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
        googlelitsents.setText( GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this));

    }
}
