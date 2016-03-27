package org.theotech.ceaselessandroid.tutorial;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by travis on 2/17/16.
 */
public class Tutorial {
    private static Tutorial ourInstance = new Tutorial();
    private static final String LAST_ACCESSED = "Tutorial_Last_Accessed_";
    private static final boolean ALWAYS_SHOW_TUTORIAL = false;

    public static Tutorial getInstance() {
        return ourInstance;
    }

    private Tutorial() {
    }

    /**
     * @param activity the context needed to get preferences
     * @param fragmentClassName the name of the fragment that the tutorial is for
     * @return whether or not the tutorial should be shown
     */
    public static boolean shouldShowTutorial(Activity activity, String fragmentClassName) {
        boolean retBool;
        String key = LAST_ACCESSED + fragmentClassName;

        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        retBool = ALWAYS_SHOW_TUTORIAL || (preferences.getLong(key, 0) == 0);
        editor.putLong(key, System.currentTimeMillis());
        editor.apply();

        return retBool;
    }
}
