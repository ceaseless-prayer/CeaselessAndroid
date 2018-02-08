package org.theotech.ceaselessandroid.tutorial;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by travis on 2/17/16.
 */
public class Tutorial {
    private static Tutorial ourInstance = new Tutorial();
    private static final String LAST_ACCESSED = "Tutorial_Last_Accessed_Date";
    private static final boolean ALWAYS_SHOW_TUTORIAL = false;

    public static Tutorial getInstance() {
        return ourInstance;
    }

    private Tutorial() {
    }

    /**
     * @param activity the context needed to get preferences
     * @return whether or not the tutorial should be shown
     */
    public static boolean shouldShowTutorial(Activity activity) {
        boolean result;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();

        result = ALWAYS_SHOW_TUTORIAL || (preferences.getLong(LAST_ACCESSED, 0) == 0);
        editor.putLong(LAST_ACCESSED, System.currentTimeMillis());
        editor.apply();

        return result;
    }
}
