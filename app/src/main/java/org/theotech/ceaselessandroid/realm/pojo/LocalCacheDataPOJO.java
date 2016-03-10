package org.theotech.ceaselessandroid.realm.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by uberx on 10/16/15.
 */
public class LocalCacheDataPOJO implements Serializable {
    private String creationDate;
    private String scriptureCitation;
    private String scriptureText;
    private String scriptureLink;
    private String scriptureJson;
    private String verseImageURL;
    private List<String> personIdsToPrayFor;
    private Integer pageIndex;

    public LocalCacheDataPOJO() {
    }

    public LocalCacheDataPOJO(String creationDate, String scriptureCitation, String scriptureText, String scriptureLink, String scriptureJson, String verseImageURL, List<String> personIdsToPrayFor, Integer cachedPageIndex) {
        this.creationDate = creationDate;
        this.scriptureCitation = scriptureCitation;
        this.scriptureText = scriptureText;
        this.scriptureLink = scriptureLink;
        this.scriptureJson = scriptureJson;
        this.verseImageURL = verseImageURL;
        this.personIdsToPrayFor = personIdsToPrayFor;
        this.pageIndex = cachedPageIndex;
    }

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

    public String getScriptureLink() {
        return scriptureLink;
    }

    public void setScriptureLink(String scriptureLink) {
        this.scriptureLink = scriptureLink;
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

    public List<String> getPersonIdsToPrayFor() {
        return personIdsToPrayFor;
    }

    public void setPersonIdsToPrayFor(List<String> personIdsToPrayFor) {
        this.personIdsToPrayFor = personIdsToPrayFor;
    }

    public Integer getPageIndex() { return pageIndex; }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }
}
