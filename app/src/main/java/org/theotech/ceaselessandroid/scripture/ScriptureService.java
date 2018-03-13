package org.theotech.ceaselessandroid.scripture;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public interface ScriptureService {
    ScriptureData getScripture();
    /**
     * This clears the cached scriptures.
     * It is used when the preferred bible version is changed.
     */
    void clearCache();

    /**
     * This is used to fill the cached scriptures
     */
    void asyncPopulateCache ();
}
