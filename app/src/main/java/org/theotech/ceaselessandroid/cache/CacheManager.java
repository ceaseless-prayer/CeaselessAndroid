package org.theotech.ceaselessandroid.cache;

/**
 * Created by uberx on 10/4/15.
 */
public interface CacheManager<T extends CacheData> {
    T getCacheData();

    void cacheData(T data);
}
