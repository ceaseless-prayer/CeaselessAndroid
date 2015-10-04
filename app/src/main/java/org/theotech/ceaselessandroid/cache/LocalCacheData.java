package org.theotech.ceaselessandroid.cache;

import org.theotech.ceaselessandroid.person.Person;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by uberx on 10/4/15.
 */
public class LocalCacheData extends RealmObject implements CacheData {
    @PrimaryKey
    private String creationDate;
    private String scriptureCitation;
    private String scriptureText;
    private String scriptureJson;
    private String verseImageURL;
    private RealmList<Person> peopleToPrayFor;

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getScriptureCitation() {
        return scriptureCitation;
    }

    public void setScriptureCitation(String scriptureCitation) {
        this.scriptureCitation = scriptureCitation;
    }

    public String getScriptureText() {
        return scriptureText;
    }

    public void setScriptureText(String scriptureText) {
        this.scriptureText = scriptureText;
    }

    public String getScriptureJson() {
        return scriptureJson;
    }

    public void setScriptureJson(String scriptureJson) {
        this.scriptureJson = scriptureJson;
    }

    public String getVerseImageURL() {
        return verseImageURL;
    }

    public void setVerseImageURL(String verseImageURL) {
        this.verseImageURL = verseImageURL;
    }

    public RealmList<Person> getPeopleToPrayFor() {
        return peopleToPrayFor;
    }

    public void setPeopleToPrayFor(RealmList<Person> peopleToPrayFor) {
        this.peopleToPrayFor = peopleToPrayFor;
    }
}
