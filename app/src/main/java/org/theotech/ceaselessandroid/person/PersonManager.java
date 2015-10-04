package org.theotech.ceaselessandroid.person;

import java.util.List;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public interface PersonManager {
    List<Person> getNextPeopleToPrayFor(int n);

    List<Person> getAllPeople();

    Person getPerson(String id);

    boolean removePerson(String id);

    boolean favoritePerson();

    boolean unfavoritePerson();
}
