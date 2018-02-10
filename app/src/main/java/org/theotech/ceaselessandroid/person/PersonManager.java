package org.theotech.ceaselessandroid.person;

import org.theotech.ceaselessandroid.realm.Person;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public interface PersonManager {

    RealmList<Person> getPersonFromPersonPOJO(List<PersonPOJO> people);

    void setRealm(Realm realm);

    List<PersonPOJO> getNextPeopleToPrayFor(int n) throws AlreadyPrayedForAllContactsException;

    List<PersonPOJO> getActivePeople();

    List<PersonPOJO> getFavoritePeople();

    List<PersonPOJO> getRemovedPeople();

    long getNumPrayed();

    long getNumPeople();

    long getNumFavoritedPeople();

    long getNumRemovedPeople();

    PersonPOJO getPerson(String personId);

    Person getRealmPerson(String personId);

    void ignorePerson(String personId);

    void unignorePerson(String personId);

    void favoritePerson(String personId);

    void unfavoritePerson(String personId);

    void populateContacts();

    List<PersonPOJO> queryPeopleByName(String query);
}
