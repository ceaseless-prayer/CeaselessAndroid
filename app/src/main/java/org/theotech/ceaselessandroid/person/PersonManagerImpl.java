package org.theotech.ceaselessandroid.person;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import org.theotech.ceaselessandroid.realm.Note;
import org.theotech.ceaselessandroid.realm.Person;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.RealmUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class PersonManagerImpl implements PersonManager {

    private static final String TAG = PersonManagerImpl.class.getSimpleName();
    private static PersonManager instance;
    private Context context;
    private Realm realm;
    private ContentResolver contentResolver;

    private PersonManagerImpl(Context context) {
        this.context = context;
        RealmConfiguration config = new RealmConfiguration.Builder(context)
                .name("org.theotech.ceaselessandroid")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        this.realm = Realm.getDefaultInstance();
        this.contentResolver = context.getContentResolver();
    }

    public static PersonManager getInstance(Context context) {
        if (instance == null) {
            instance = new PersonManagerImpl(context);
        }
        return instance;
    }

    @Override
    public List<PersonPOJO> getNextPeopleToPrayFor(int n) throws AlreadyPrayedForAllContactsException {
        List<PersonPOJO> people = new ArrayList<>();
        RealmResults<Person> results = realm.where(Person.class).equalTo("ignored", false).equalTo("prayed", false).findAllSorted("lastPrayed");
        RealmResults<Person> favoritedResults = realm.where(Person.class).equalTo("favorite", true).findAllSorted("lastPrayed");

        // if all people are prayed for, then reset and throw exception
        if (getNumPeople() > 0 && results.size() == 0) {
            // Reset all the prayed flags
            realm.beginTransaction();
            RealmResults<Person> resultsToReset = realm.where(Person.class).equalTo("ignored", false).findAll();
            for (int i = 0; i < resultsToReset.size(); i++) {
                resultsToReset.get(i).setPrayed(false);
            }
            realm.commitTransaction();
            // Throw Exception
            throw new AlreadyPrayedForAllContactsException(getNumPeople());
        }

        List<Person> favoritedPeople = new ArrayList<>();
        for (int i = 0; i < favoritedResults.size(); i++) {
            favoritedPeople.add(favoritedResults.get(i));
        }

        if (favoritedPeople.size() > 0) {
            if (favoritedPeople.size() < 4) {
                // 1/4 chance of getting a favorited person
                Random random = new Random();
                if (random.nextInt(4) == 0) {
                    selectPerson(favoritedPeople.get(0), people);
                }
            } else {
                // always show a favorited person if they have
                // favorited more than 7 people
                selectPerson(favoritedPeople.get(0), people);
            }

        }

        // shuffle the list of people
        List<Person> allPeople = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            allPeople.add(results.get(i));
        }
        Collections.shuffle(allPeople);

        // Get N people, and set last prayed and the prayed flag
        if (allPeople.size() < 1) {
            return people;
        }

        // select the desired number of people.
        // Excluding anyone already favorited who has already been selected
        Integer numToSelect = Math.min(n, allPeople.size());
        int i = 0;
        while (people.size() < numToSelect) {
            Person person = allPeople.get(i);
            if (!people.contains(getPerson(person.getId()))) {
                // select this person if they haven't been chosen yet
                selectPerson(person, people);
            }
            i++;
        }

        return people;
    }

    private void selectPerson(Person person, List<PersonPOJO> people) {
        realm.beginTransaction();
        person.setLastPrayed(new Date());
        person.setPrayed(true);
        realm.commitTransaction();
        people.add(getPerson(person.getId()));
        Log.d(TAG, "Selecting person " + person);
    }

    @Override
    public List<PersonPOJO> getActivePeople() {
        return RealmUtils.toPersonPOJOs(realm.where(Person.class).equalTo("ignored", false).findAllSorted("name"));
    }

    @Override
    public List<PersonPOJO> getRemovedPeople() {
        return RealmUtils.toPersonPOJOs(realm.where(Person.class).equalTo("ignored", true).findAllSorted("name"));
    }

    @Override
    public long getNumPrayed() {
        return realm.where(Person.class).equalTo("ignored", false).equalTo("prayed", true).count();
    }

    @Override
    public long getNumPeople() {
        return realm.where(Person.class).equalTo("ignored", false).count();
    }

    @Override
    public PersonPOJO getPerson(String personId) {
        return RealmUtils.toPersonPOJO(getRealmPerson(personId));
    }

    private Person getRealmPerson(String personId) {
        return realm.where(Person.class).equalTo("id", personId).findFirst();
    }

    @Override
    public void ignorePerson(String personId) {
        realm.beginTransaction();
        Person person = getRealmPerson(personId);
        person.setIgnored(true);
        person.setFavorite(false);
        realm.commitTransaction();
    }

    @Override
    public void unignorePerson(String personId) {
        realm.beginTransaction();
        getRealmPerson(personId).setIgnored(false);
        realm.commitTransaction();
    }

    @Override
    public void favoritePerson(String personId) {
        realm.beginTransaction();
        getRealmPerson(personId).setFavorite(true);
        realm.commitTransaction();
    }

    @Override
    public void unfavoritePerson(String personId) {
        realm.beginTransaction();
        getRealmPerson(personId).setFavorite(false);
        realm.commitTransaction();
    }

    @Override
    public void addNote(String personId, String title, String text) {
        realm.beginTransaction();
        Note note = realm.createObject(Note.class);
        note.setCreationDate(new Date());
        note.setLastUpdatedDate(new Date());
        note.setId(UUID.randomUUID().toString());
        if (title != null)
            note.setTitle(title);
        note.setText(text);
        getRealmPerson(personId).getNotes().add(note);
        realm.commitTransaction();
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @Override
    public void populateContacts() {
        Cursor cursor = null;
        realm.beginTransaction();
        int added = 0;
        int updated = 0;
        try {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            while (cursor.moveToNext()) {
                if (isValidContact(cursor)) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.d(TAG, String.format("Person: id=%s name=%s", id, name));

                    Person person = realm.where(Person.class).equalTo("id", id).findFirst();
                    if (person == null) {
                        person = realm.createObject(Person.class);
                        person.setId(id);
                        person.setName(name);
                        person.setSource("Contacts");
                        person.setIgnored(false);
                        person.setLastPrayed(new Date(0l));
                        ++added;
                    } else {
                        Log.d(TAG, "User already existed, updating information.");
                        // Update the name in case the user updates it
                        person.setName(name);
                        ++updated;
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        realm.commitTransaction();

        Log.d(TAG, String.format("Successfully added %d and updated %d contacts.", added, updated));
    }

    private boolean isValidContact(Cursor cursor) {
        boolean hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1;
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        // TODO filter out "Conference Bridge" and "Directory Assistance" as other common things to ignore
        return hasPhoneNumber && !name.startsWith("#");
    }

}
