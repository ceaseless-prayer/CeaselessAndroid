package org.theotech.ceaselessandroid.util;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Created by chrislim on 1/22/16.
 * Note in the android M there is an auto-backup service: http://developer.android.com/training/backup/autosyncapi.html
 */
public class AppBackupAgentHelper extends BackupAgentHelper {

    static final String PREFS_BACKUP_KEY = "appprefs";
    static final String FILES_BACKUP_KEY = "appfiles";

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, Constants.DEFAULT_PREFERENCES_FILE);
        addHelper(PREFS_BACKUP_KEY, helper);
        FileBackupHelper fileBackupHelper = new FileBackupHelper(this, Constants.REALM_FILE_NAME);
        addHelper(FILES_BACKUP_KEY, fileBackupHelper);
    }
}
