package org.theotech.ceaselessandroid.cache;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by uberx on 10/4/15.
 */
public class LocalDailyCacheManagerImpl implements CacheManager<LocalCacheData> {
    private static final String TAG = LocalDailyCacheManagerImpl.class.getSimpleName();

    private static LocalDailyCacheManagerImpl instance = null;
    private Realm realm;

    public LocalDailyCacheManagerImpl(Context context) {
        RealmConfiguration config = new RealmConfiguration.Builder(context)
                .name("org.theotech.ceaselessandroid")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        this.realm = Realm.getDefaultInstance();
    }

    public static LocalDailyCacheManagerImpl getInstance(Context context) {
        if (instance == null) {
            instance = new LocalDailyCacheManagerImpl(context);
        }
        return instance;
    }

    @Override
    public LocalCacheData getCacheData() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return realm.where(LocalCacheData.class).equalTo("creationDate",
                String.format("%s-%s-%s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH))).findFirst();
    }

    @Override
    public void cacheData(LocalCacheData data) {
        realm.beginTransaction();

        LocalCacheData cacheData = realm.where(LocalCacheData.class).equalTo("creationDate", data.getCreationDate()).findFirst();
        if (cacheData == null) {
            cacheData = realm.createObject(LocalCacheData.class);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            cacheData.setCreationDate(String.format("%s-%s-%s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
            cacheData.setPeopleToPrayFor(data.getPeopleToPrayFor());
            cacheData.setScriptureCitation(data.getScriptureCitation());
            cacheData.setScriptureText(data.getScriptureText());
            cacheData.setVerseImageURL(data.getVerseImageURL());
        } else {
            cacheData.setPeopleToPrayFor(data.getPeopleToPrayFor());
            cacheData.setScriptureCitation(data.getScriptureCitation());
            cacheData.setScriptureText(data.getScriptureText());
            cacheData.setVerseImageURL(data.getVerseImageURL());
        }

        realm.commitTransaction();
    }
}
