package org.theotech.ceaselessandroid;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.util.Constants;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import com.onesignal.OneSignal;

/**
 * Created by Andrew Ma on 10/5/15.
 */
public class CeaselessApplication extends Application {

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // crashlytics
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        // OneSignal setup
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        // picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso picasso = builder.build();
        Picasso.setSingletonInstance(picasso);

        Iconify.with(new FontAwesomeModule());

        // realm (added by T. Kopp on 2/2/16)
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Constants.REALM_FILE_NAME)
                .schemaVersion(Constants.SCHEMA_VERSION)
                .build();
        // ajma: removing .deleteRealmIfMigrationNeeded() because this results in a silent data loss.
        //       We'd rather it crash (rollout with 5% first to see) and then patch so we don't loose people's data.
        Realm.setDefaultConfiguration(config);

    }
}
