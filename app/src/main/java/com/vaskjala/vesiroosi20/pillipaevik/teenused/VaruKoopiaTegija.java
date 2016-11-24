package com.vaskjala.vesiroosi20.pillipaevik.teenused;

import android.app.backup.*;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.vaskjala.vesiroosi20.pillipaevik.BuildConfig;
import com.vaskjala.vesiroosi20.pillipaevik.R;
import com.vaskjala.vesiroosi20.pillipaevik.teenused.PilliPaevikDatabase;

import java.io.File;
import java.io.IOException;

/**
 * Created by mihkel on 5.06.2016.
 */
public class VaruKoopiaTegija extends BackupAgentHelper {

    @Override
    public File getFilesDir(){
        File path = getDatabasePath(PilliPaevikDatabase.DATABASE_NAME);
        return path.getParentFile();
    }

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helperSP =
                new SharedPreferencesBackupHelper(this, getString(R.string.seadete_fail));
        addHelper(getString(R.string.seadete_fail), helperSP);

        FileBackupHelper helperDB = new FileBackupHelper(this, PilliPaevikDatabase.DATABASE_NAME);
        addHelper(PilliPaevikDatabase.DATABASE_NAME, helperDB);
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
