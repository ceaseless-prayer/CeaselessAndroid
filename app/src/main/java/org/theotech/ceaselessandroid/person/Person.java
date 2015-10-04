package org.theotech.ceaselessandroid.person;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class Person {
    private final String id;
    private final String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
