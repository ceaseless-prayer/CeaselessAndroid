package org.theotech.ceaselessandroid.util;

import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by chrislim on 1/11/16.
 */
public class AnalyticsUtils {

    private static final String TAG = AnalyticsUtils.class.getSimpleName();

    public static void sendScreenViewHit(Tracker mTracker, String name) {
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
