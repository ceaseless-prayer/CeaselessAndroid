package org.theotech.ceaselessandroid.cache;

import android.content.Context;

import org.theotech.ceaselessandroid.realm.LocalCacheData;
import org.theotech.ceaselessandroid.realm.RealmString;
import org.theotech.ceaselessandroid.realm.pojo.LocalCacheDataPOJO;
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.util.RealmUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

/**
 * Created by uberx on 10/4/15.
 */
public class LocalDailyCacheManagerImpl implements CacheManager {
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
    public ScriptureData getCachedScripture() {
        LocalCacheDataPOJO cacheData = getCacheData();
        if (cacheData != null &&
                cacheData.getScriptureText() != null && !cacheData.getScriptureText().isEmpty() &&
                cacheData.getScriptureCitation() != null && !cacheData.getScriptureCitation().isEmpty() &&
                cacheData.getScriptureJson() != null && !cacheData.getScriptureJson().isEmpty()) {
            return new ScriptureData(cacheData.getScriptureText(), cacheData.getScriptureCitation(), cacheData.getScriptureJson());
        }
        return null;
    }

    @Override
    public void cacheScripture(ScriptureData scriptureData) {
        if (scriptureData != null &&
                scriptureData.getText() != null && !scriptureData.getText().isEmpty() &&
                scriptureData.getCitation() != null && !scriptureData.getCitation().isEmpty() &&
                scriptureData.getJson() != null && !scriptureData.getJson().isEmpty()) {
            LocalCacheDataPOJO newCacheData = new LocalCacheDataPOJO();
            newCacheData.setScriptureText(scriptureData.getText());
            newCacheData.setScriptureCitation(scriptureData.getCitation());
            newCacheData.setScriptureJson(scriptureData.getJson());
            cacheData(newCacheData);
        }
    }

    @Override
    public String getCachedVerseImageURL() {
        LocalCacheDataPOJO cacheData = getCacheData();
        if (cacheData != null &&
                cacheData.getVerseImageURL() != null && !cacheData.getVerseImageURL().isEmpty()) {
            return cacheData.getVerseImageURL();
        }
        return null;
    }

    @Override
    public void cacheVerseImageURL(String verseImageURL) {
        if (verseImageURL != null && !verseImageURL.isEmpty()) {
            LocalCacheDataPOJO newCacheData = new LocalCacheDataPOJO();
            newCacheData.setVerseImageURL(verseImageURL);
            cacheData(newCacheData);
        }
    }

    @Override
    public List<String> getCachedPersonIdsToPrayFor() {
        LocalCacheDataPOJO cacheData = getCacheData();
        if (cacheData != null &&
                cacheData.getPersonIdsToPrayFor() != null && !cacheData.getPersonIdsToPrayFor().isEmpty()) {
            return cacheData.getPersonIdsToPrayFor();
        }
        return null;
    }

    @Override
    public void cachePersonIdsToPrayFor(List<String> personIdsToPrayFor) {
        if (personIdsToPrayFor != null && !personIdsToPrayFor.isEmpty()) {
            LocalCacheDataPOJO newCacheData = new LocalCacheDataPOJO();
            newCacheData.setPersonIdsToPrayFor(personIdsToPrayFor);
            cacheData(newCacheData);
        }
    }

    private LocalCacheDataPOJO getCacheData() {
        return RealmUtils.toLocalCacheDataPOJO(getRealmCacheData());
    }

    private LocalCacheData getRealmCacheData() {
        return realm.where(LocalCacheData.class).equalTo("creationDate", generateCreationDate()).findFirst();
    }

    private void cacheData(LocalCacheDataPOJO newCacheData) {
        realm.beginTransaction();

        LocalCacheData realmCacheData = getRealmCacheData();
        if (realmCacheData == null) {
            realmCacheData = realm.createObject(LocalCacheData.class);
        }
        populateCacheData(realmCacheData, newCacheData);

        realm.commitTransaction();
    }

    private void populateCacheData(LocalCacheData realmCacheData, LocalCacheDataPOJO newCacheData) {
        realmCacheData.setCreationDate(generateCreationDate());
        if (newCacheData.getPersonIdsToPrayFor() != null) {
            List<String> personIdsToPrayFor = newCacheData.getPersonIdsToPrayFor();
            RealmList<RealmString> managedPersonIdsToPrayFor = new RealmList<>();
            for (String personIdToPrayFor : personIdsToPrayFor) {
                managedPersonIdsToPrayFor.add(realm.copyToRealm(new RealmString(personIdToPrayFor)));
            }
            realmCacheData.setPersonIdsToPrayFor(managedPersonIdsToPrayFor);
        }
        if (newCacheData.getScriptureCitation() != null)
            realmCacheData.setScriptureCitation(newCacheData.getScriptureCitation());
        if (newCacheData.getScriptureText() != null)
            realmCacheData.setScriptureText(newCacheData.getScriptureText());
        if (newCacheData.getScriptureJson() != null)
            realmCacheData.setScriptureJson(newCacheData.getScriptureJson());
        if (newCacheData.getVerseImageURL() != null)
            realmCacheData.setVerseImageURL(newCacheData.getVerseImageURL());
    }
}
