package org.theotech.ceaselessandroid.person;

import org.theotech.ceaselessandroid.realm.Note;
import org.theotech.ceaselessandroid.realm.Person;

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

    Person getPerson(String personId);

    boolean ignorePerson(String personId);

    boolean unignorePerson(String ipersonIdd);

    boolean favoritePerson(String personId);

    boolean unfavoritePerson(String personId);

    void addNote(String personId, String title, String text);

    void editNote(String noteId, String title, String text);

    void removeNote(String noteId);

    Note getNote(String noteId);

    void tagNote(String noteId, String personId);

    void untagNote(String noteId, String personId);

    void populateContacts();
}
