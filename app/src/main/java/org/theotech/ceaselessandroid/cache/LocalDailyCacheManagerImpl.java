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

    public static String generateCreationDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return String.format("%s-%s-%s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public LocalCacheData getCacheData() {
        return realm.where(LocalCacheData.class).equalTo("creationDate", generateCreationDate()).findFirst();
    }

    @Override
    public void cacheData(LocalCacheData data) {
        realm.beginTransaction();

        LocalCacheData cacheData = realm.where(LocalCacheData.class).equalTo("creationDate", data.getCreationDate()).findFirst();
        if (cacheData == null) {
            cacheData = realm.createObject(LocalCacheData.class);
            cacheData.setCreationDate(generateCreationDate());
        }
        populateCacheData(cacheData, data);

        realm.commitTransaction();
    }

    private void populateCacheData(LocalCacheData realmCacheData, LocalCacheData data) {
        if (data.getPeopleToPrayFor() != null)
            realmCacheData.setPeopleToPrayFor(data.getPeopleToPrayFor());
        if (data.getScriptureCitation() != null)
            realmCacheData.setScriptureCitation(data.getScriptureCitation());
        if (data.getScriptureText() != null)
            realmCacheData.setScriptureText(data.getScriptureText());
        if (data.getScriptureJson() != null)
            realmCacheData.setScriptureJson(data.getScriptureJson());
        if (data.getVerseImageURL() != null)
            realmCacheData.setVerseImageURL(data.getVerseImageURL());
    }
}
