package org.theotech.ceaselessandroid.cache;

import android.content.Context;

import org.theotech.ceaselessandroid.realm.LocalCacheData;
import org.theotech.ceaselessandroid.realm.RealmString;
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
        LocalCacheData cacheData = getCacheData();
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
            LocalCacheData cacheData = new LocalCacheData();
            cacheData.setScriptureText(scriptureData.getText());
            cacheData.setScriptureCitation(scriptureData.getCitation());
            cacheData.setScriptureJson(scriptureData.getJson());
            cacheData(cacheData);
        }
    }

    @Override
    public String getCachedVerseImageURL() {
        LocalCacheData cacheData = getCacheData();
        if (cacheData != null &&
                cacheData.getVerseImageURL() != null && !cacheData.getVerseImageURL().isEmpty()) {
            return cacheData.getVerseImageURL();
        }
        return null;
    }

    @Override
    public void cacheVerseImageURL(String verseImageURL) {
        if (verseImageURL != null && !verseImageURL.isEmpty()) {
            LocalCacheData cacheData = new LocalCacheData();
            cacheData.setVerseImageURL(verseImageURL);
            cacheData(cacheData);
        }
    }

    @Override
    public List<String> getCachedPersonIdsToPrayFor() {
        LocalCacheData cacheData = getCacheData();
        if (cacheData != null &&
                cacheData.getPersonIdsToPrayFor() != null && !cacheData.getPersonIdsToPrayFor().isEmpty()) {
            return RealmUtils.convert(cacheData.getPersonIdsToPrayFor());
        }
        return null;
    }

    @Override
    public void cachePersonIdsToPrayFor(List<String> personIdsToPrayFor) {
        if (personIdsToPrayFor != null && !personIdsToPrayFor.isEmpty()) {
            LocalCacheData cacheData = new LocalCacheData();
            cacheData.setPersonIdsToPrayFor(RealmUtils.convert(personIdsToPrayFor));
            cacheData(cacheData);
        }
    }

    private LocalCacheData getCacheData() {
        return realm.where(LocalCacheData.class).equalTo("creationDate", generateCreationDate()).findFirst();
    }

    private void cacheData(LocalCacheData data) {
        realm.beginTransaction();

        LocalCacheData newCacheData = getCacheData();
        if (newCacheData == null) {
            newCacheData = realm.createObject(LocalCacheData.class);
        }
        populateCacheData(newCacheData, data);

        realm.commitTransaction();
    }

    private void populateCacheData(LocalCacheData newCacheData, LocalCacheData existingCacheData) {
        newCacheData.setCreationDate(generateCreationDate());
        if (existingCacheData.getPersonIdsToPrayFor() != null) {
            RealmList<RealmString> personIdsToPrayFor = existingCacheData.getPersonIdsToPrayFor();
            RealmList<RealmString> managedPersonIdsToPrayFor = new RealmList<RealmString>();
            for (RealmString personIdToPrayFor : personIdsToPrayFor) {
                managedPersonIdsToPrayFor.add(realm.copyToRealm(new RealmString(personIdToPrayFor.getString())));
            }
            newCacheData.setPersonIdsToPrayFor(managedPersonIdsToPrayFor);
        }
        if (existingCacheData.getScriptureCitation() != null)
            newCacheData.setScriptureCitation(existingCacheData.getScriptureCitation());
        if (existingCacheData.getScriptureText() != null)
            newCacheData.setScriptureText(existingCacheData.getScriptureText());
        if (existingCacheData.getScriptureJson() != null)
            newCacheData.setScriptureJson(existingCacheData.getScriptureJson());
        if (existingCacheData.getVerseImageURL() != null)
            newCacheData.setVerseImageURL(existingCacheData.getVerseImageURL());
    }
}
