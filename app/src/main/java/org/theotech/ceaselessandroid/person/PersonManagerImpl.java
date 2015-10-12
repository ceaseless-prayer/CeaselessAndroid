package org.theotech.ceaselessandroid.person;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import org.theotech.ceaselessandroid.realm.Note;
import org.theotech.ceaselessandroid.realm.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
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
    private List<Person> people = null;

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
        populateContacts();
    }

    public static PersonManager getInstance(Context context) {
        if (instance == null) {
            instance = new PersonManagerImpl(context);
        }
        return instance;
    }

    @Override
    public List<Person> getNextPeopleToPrayFor(int n) throws AlreadyPrayedForAllContactsException {
        List<Person> people = new ArrayList<Person>();
        RealmResults<Person> results = realm.where(Person.class).equalTo("ignored", false).equalTo("prayed", false).findAllSorted("lastPrayed");

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

        // shuffle the list of people
        List<Person> allPeople = new ArrayList<Person>();
        for (int i = 0; i < results.size(); i++) {
            allPeople.add(results.get(i));
        }
        Collections.shuffle(allPeople);

        // Get N people, and set last prayed and the prayed flag
        if (allPeople.size() < 1) {
            return people;
        }

        for (int i = 0; i < Math.min(n, allPeople.size()); i++) {
            realm.beginTransaction();
            Person person = allPeople.get(i);
            person.setLastPrayed(new Date());
            person.setPrayed(true);
            realm.commitTransaction();
            people.add(getPerson(person.getId()));
        }

        return people;
    }

    @Override
    public List<Person> getAllPeople() {
        return realm.where(Person.class).equalTo("ignored", false).findAll();
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
    public Person getPerson(String personId) {
        return realm.where(Person.class).equalTo("id", personId).findFirst();
    }

    @Override
    public boolean ignorePerson(String personId) {
        realm.beginTransaction();
        getPerson(personId).setIgnored(true);
        realm.commitTransaction();
        return true;
    }

    @Override
    public boolean unignorePerson(String personId) {
        realm.beginTransaction();
        getPerson(personId).setIgnored(false);
        realm.commitTransaction();
        return true;
    }

    @Override
    public boolean favoritePerson(String personId) {
        realm.beginTransaction();
        getPerson(personId).setFavorite(true);
        realm.commitTransaction();
        return true;
    }

    @Override
    public boolean unfavoritePerson(String personId) {
        realm.beginTransaction();
        getPerson(personId).setFavorite(false);
        realm.commitTransaction();
        return false;
    }

    @Override
    public void addNote(String personId, String text) {
        Note note = new Note();
        note.setCreationDate(new Date());
        note.setLastUpdatedDate(new Date());
        note.setId(UUID.randomUUID().toString());
        note.setText(text);

        realm.beginTransaction();
        Person person = getPerson(personId);
        RealmList<Note> notes = person.getNotes();
        notes.add(note);
        person.setNotes(notes);
        realm.commitTransaction();
    }

    @Override
    public void editNote(String noteId, String text) {
        realm.beginTransaction();
        Note note = getNote(noteId);
        note.setLastUpdatedDate(new Date());
        note.setText(text);
        realm.commitTransaction();
    }

    @Override
    public void removeNote(String personId, String noteId) {
        realm.beginTransaction();
        Person person = getPerson(personId);
        RealmList<Note> notes = person.getNotes();
        RealmList<Note> newNotes = new RealmList<Note>();
        for (Note note : notes) {
            if (!note.getId().equals(noteId)) {
                newNotes.add(note);
            }
        }
        person.setNotes(newNotes);
        realm.commitTransaction();
    }

    @Override
    public Note getNote(String noteId) {
        return realm.where(Note.class).equalTo("id", noteId).findFirst();
    }

    @Override
    public void tagNote(String noteId, String personId) {

    }

    @Override
    public void untagNote(String noteId, String personId) {

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
        return hasPhoneNumber;
    }

}
