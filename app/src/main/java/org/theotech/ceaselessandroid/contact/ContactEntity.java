package org.theotech.ceaselessandroid.contact;

/**
 * Created by uberx on 10/3/15.
 */
public class ContactEntity {
    private final String id;
    private final String name;

    public ContactEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
