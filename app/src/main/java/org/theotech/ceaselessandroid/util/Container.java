package org.theotech.ceaselessandroid.util;

/**
 * Created by uberx on 10/31/15.
 */
public class Container<T> {
    private T obj;

    public Container() {
    }

    public Container(T obj) {
        this.obj = obj;
    }

    public T get() {
        return obj;
    }

    public void set(T obj) {
        this.obj = obj;
    }
}
