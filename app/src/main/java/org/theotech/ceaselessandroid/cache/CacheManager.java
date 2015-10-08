package org.theotech.ceaselessandroid.cache;

import org.theotech.ceaselessandroid.person.Person;
import org.theotech.ceaselessandroid.scripture.ScriptureData;

import java.util.List;

/**
 * Created by uberx on 10/4/15.
 */
public interface CacheManager {
    ScriptureData getCachedScripture();

    void cacheScripture(ScriptureData scriptureData);

    String getCachedVerseImageURL();

    void cacheVerseImageURL(String verseImageURL);

    List<Person> getCachedPeopleToPrayFor();

    void cachePeopleToPrayFor(List<Person> peopleToPrayFor);
}
