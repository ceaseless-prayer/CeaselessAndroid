package org.theotech.ceaselessandroid.person;

import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public interface PersonManager {

    void setRealm(Realm realm);

    List<PersonPOJO> getNextPeopleToPrayFor(int n) throws AlreadyPrayedForAllContactsException;

    List<PersonPOJO> getActivePeople();

    List<PersonPOJO> getRemovedPeople();

    long getNumPrayed();

    long getNumPeople();

    PersonPOJO getPerson(String personId);

    void ignorePerson(String personId);

    void unignorePerson(String personId);

    void favoritePerson(String personId);

    void unfavoritePerson(String personId);

    void addNote(String personId, String title, String text);

    void populateContacts();
}
