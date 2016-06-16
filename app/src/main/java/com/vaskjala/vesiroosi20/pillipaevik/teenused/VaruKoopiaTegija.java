package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.backup.*;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;

import java.io.IOException;

/**
 * Created by mihkel on 5.06.2016.
 */
public class VaruKoopiaTegija extends BackupAgentHelper {
    // The name of the SharedPreferences file
    private static final String PREFS = "seadete_fail";
    private String currentDBPath = "/data/data/com.vaskjala.vesiroosi20.pillipaevik/databases/" + PilliPaevikDatabase.DATABASE_NAME;


    // A key to uniquely identify the set of backup data
    private static final String PREFS_BACKUP_KEY = "seaded";
    private static final String FILES_BACKUP_KEY = PilliPaevikDatabase.DATABASE_NAME;

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        Log.d("VaruKoopiaTegija","onCreate");
        SharedPreferencesBackupHelper helperSP =
                new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(PREFS_BACKUP_KEY, helperSP);

        FileBackupHelper helperDB = new FileBackupHelper(this, currentDBPath);
        addHelper(FILES_BACKUP_KEY, helperDB);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        synchronized (PilliPaevikDatabase.sPilliPaevikuLukk) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        synchronized (PilliPaevikDatabase.sPilliPaevikuLukk) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
}
