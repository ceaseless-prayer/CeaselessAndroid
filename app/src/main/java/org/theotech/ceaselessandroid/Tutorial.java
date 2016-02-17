package org.theotech.ceaselessandroid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by travis on 2/17/16.
 */
public class Tutorial {
    private static Tutorial ourInstance = new Tutorial();
    private static final String LAST_ACCESSED = "Last_Accessed_";
    private static final boolean DEBUG_MODE = false;

    public static Tutorial getInstance() {
        return ourInstance;
    }

    private Tutorial() {
    }

    public static boolean shouldShowTutorial(Activity activity, String app_Context) {
        boolean retBool;
        String key = LAST_ACCESSED + app_Context;

        SharedPreferences shPr = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shPr.edit();

        retBool = DEBUG_MODE || (shPr.getLong(key, 0) == 0);
        editor.putLong(key, System.currentTimeMillis());
        editor.apply();

        return retBool;
    }
}
