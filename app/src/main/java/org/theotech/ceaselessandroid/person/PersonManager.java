package org.theotech.ceaselessandroid.person;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public interface PersonManager {

    void setRealm(Realm realm);

    List<Person> getNextPeopleToPrayFor(int n) throws AlreadyPrayedForAllContactsException;

    List<Person> getAllPeople();

    long getNumPrayed();

    long getNumPeople();

    Person getPerson(String id);

    boolean ignorePerson(String id);

    boolean unignorePerson(String id);

    boolean favoritePerson(String i);

    boolean unfavoritePerson(String i);

    void populateContacts();
}
