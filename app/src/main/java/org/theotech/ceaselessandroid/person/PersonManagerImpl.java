package org.theotech.ceaselessandroid.person;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class PersonManagerImpl implements PersonManager {

    private Realm realm;

    @Override
    public List<Person> getNextPeopleToPrayFor(int n) {
        return null;
    }

    @Override
    public List<Person> getAllPeople() {
        return null;
    }

    @Override
    public Person getPerson(String id) {
        return null;
    }

    @Override
    public boolean removePerson(String id) {
        return false;
    }

    @Override
    public boolean favoritePerson() {
        return false;
    }

    @Override
    public boolean unfavoritePerson() {
        return false;
    }
}
