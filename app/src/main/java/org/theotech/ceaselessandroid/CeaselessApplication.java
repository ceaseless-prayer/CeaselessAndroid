package org.theotech.ceaselessandroid;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Andrew Ma on 10/5/15.
 */
public class CeaselessApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // crashlytics
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        // picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso picasso = builder.build();
        Picasso.setSingletonInstance(picasso);

        Iconify.with(new FontAwesomeModule());
    }
}
