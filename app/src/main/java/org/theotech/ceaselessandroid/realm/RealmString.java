package org.theotech.ceaselessandroid.realm;

import io.realm.RealmObject;

/**
 * Created by uberx on 10/4/15.
 */
public class RealmString extends RealmObject {
    private String string;

    public RealmString(String string) {
        this.string = string;
    }

    public RealmString() {
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
