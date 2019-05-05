package org.theotech.ceaselessandroid.util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by chrislim on 1/11/16.
 */
public class AnalyticsUtils {

    private static final String TAG = AnalyticsUtils.class.getSimpleName();

    public static void sendScreenViewHit(Activity activity, String name) {
        Log.v(TAG, "Setting screen name: " + name);
        FirebaseAnalytics.getInstance(activity).setCurrentScreen(activity, name, null /* class override */);
    }

    public static void sendEventWithCategoryAndValue(Activity activity, String category, String action, String label, Long value) {
        final Bundle params = new Bundle();
        params.putString("category", category);
        params.putString("action", action);
        params.putString("label", label);
        params.putLong("value", value);
        FirebaseAnalytics.getInstance(activity).logEvent("ga_event", params);
    }

    public static void sendEventWithCategory(Activity activity, String category, String action, String label) {
        final Bundle params = new Bundle();
        params.putString("category", category);
        params.putString("action", action);
        params.putString("label", label);
        FirebaseAnalytics.getInstance(activity).logEvent("ga_event", params);
    }
}
