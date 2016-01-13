package org.theotech.ceaselessandroid.person;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import org.theotech.ceaselessandroid.CeaselessApplication;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.realm.Person;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.AnalyticsUtils;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.Installation;
import org.theotech.ceaselessandroid.util.RealmUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class PersonManagerImpl implements PersonManager {
    private static final String TAG = PersonManagerImpl.class.getSimpleName();
    private static final String CONTACTS_SOURCE = "Contacts";
    private static final int RANDOM_FAVORITE_THRESHOLD = 4;
    private static final int RANDOM_SAMPLE_POST_METRICS = 2;

    private static PersonManager instance;
    private Activity activity;
    private Realm realm;
    private ContentResolver contentResolver;
    private Tracker mTracker;

    private PersonManagerImpl(Activity activity) {
        this.activity = activity;
        RealmConfiguration config = new RealmConfiguration.Builder(this.activity)
                .name(Constants.REALM_FILE_NAME)
                .schemaVersion(Constants.SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        this.realm = Realm.getDefaultInstance();
        this.contentResolver = this.activity.getContentResolver();

        // setup analytics
        CeaselessApplication application = (CeaselessApplication) activity.getApplication();
        mTracker = application.getDefaultTracker();
    }

    public static PersonManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new PersonManagerImpl(activity);
        }
        return instance;
    }

    @Override
    public RealmList<Person> getPersonFromPersonPOJO(List<PersonPOJO> people) {
        RealmList<Person> listOfPersons = new RealmList<>();

        if (people == null || people.size() == 0) {
            return listOfPersons;
        }

        RealmQuery<Person> query = realm.where(Person.class)
                .equalTo(Person.Column.ID, people.get(0).getId());

        for (int i = 1; i < people.size(); i++) {
            query = query.or().equalTo(Person.Column.ID, people.get(i).getId());
        }

        RealmResults<Person> results = query.findAll();
        for (int i = 0; i < results.size(); i++) {
            listOfPersons.add(results.get(i));
        }

        return listOfPersons;
    }

    @Override
    public List<PersonPOJO> getNextPeopleToPrayFor(int n) throws AlreadyPrayedForAllContactsException {
        List<PersonPOJO> people = new ArrayList<>();
        RealmResults<Person> results = realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, false)
                .equalTo(Person.Column.PRAYED, false)
                .findAllSorted(Person.Column.LAST_PRAYED);
        RealmResults<Person> favoritedResults = realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.FAVORITE, true)
                .findAllSorted(Person.Column.LAST_PRAYED);

        // if all people are prayed for, then reset and throw exception
        if (getNumPeople() > 0 && results.size() == 0) {
            // Reset all the prayed flags
            realm.beginTransaction();
            RealmResults<Person> resultsToReset = realm.where(Person.class)
                    .equalTo(Person.Column.ACTIVE, true)
                    .equalTo(Person.Column.IGNORED, false)
                    .findAll();
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
            if (favoritedPeople.size() < RANDOM_FAVORITE_THRESHOLD) {
                // 1/4 chance of getting a favorited person
                Random random = new Random();
                if (random.nextInt(RANDOM_FAVORITE_THRESHOLD) == 0) {
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
        return RealmUtils.toPersonPOJOs(realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, false)
                .findAllSorted(Person.Column.NAME));
    }

    @Override
    public List<PersonPOJO> getRemovedPeople() {
        return RealmUtils.toPersonPOJOs(realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, true)
                .findAllSorted(Person.Column.NAME));
    }

    @Override
    public long getNumPrayed() {
        return realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, false)
                .equalTo(Person.Column.PRAYED, true)
                .count();
    }

    @Override
    public long getNumPeople() {
        return realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, false)
                .count();
    }

    @Override
    public long getNumFavoritedPeople() {
        return realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, false)
                .equalTo(Person.Column.FAVORITE, true)
                .count();
    }

    @Override
    public long getNumRemovedPeople() {
        return realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, true)
                .count();
    }

    @Override
    public PersonPOJO getPerson(String personId) {
        return RealmUtils.toPersonPOJO(getRealmPerson(personId));
    }

    @Override
    public Person getRealmPerson(String personId) {
        return realm.where(Person.class)
                .equalTo(Person.Column.ID, personId)
                .findFirst();
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
                    Log.v(TAG, String.format("Person: id=%s name=%s", id, name));

                    Person person = realm.where(Person.class)
                            .equalTo(Person.Column.ID, id)
                            .findFirst();
                    if (person == null) {
                        person = realm.createObject(Person.class);
                        person.setId(id);
                        person.setName(name);
                        person.setSource(CONTACTS_SOURCE);
                        person.setActive(true);
                        person.setIgnored(false);
                        person.setLastPrayed(new Date(0L));
                        ++added;
                    } else {
                        Log.v(TAG, "User already existed, updating information.");
                        // Update the name in case the user updates it
                        person.setName(name);
                        person.setActive(true);
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
        sampleAndPostMetrics();
    }

    private boolean isValidContact(Cursor cursor) {
        boolean hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1;
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        // TODO: filter out "Conference Bridge" and "Directory Assistance" as other common things to ignore
        return hasPhoneNumber && !name.startsWith("#");
    }

    private void sampleAndPostMetrics() {
        Random random = new Random();
        if (random.nextInt(RANDOM_SAMPLE_POST_METRICS) == 0) {
            Log.i(TAG, "Posting contact metrics for analytics");
            String installationId = Installation.id(activity);
            AnalyticsUtils.sendEventWithCategoryAndValue(mTracker,
                    getString(R.string.ga_address_book_sync),
                    getString(R.string.ga_post_total_active_contacts),
                    installationId,
                    getNumPeople());

            AnalyticsUtils.sendEventWithCategoryAndValue(mTracker,
                    getString(R.string.ga_address_book_sync),
                    getString(R.string.ga_post_total_favorited),
                    installationId,
                    getNumFavoritedPeople());

            AnalyticsUtils.sendEventWithCategoryAndValue(mTracker,
                    getString(R.string.ga_address_book_sync),
                    getString(R.string.ga_post_total_removed_contacts),
                    installationId,
                    getNumRemovedPeople());

            AnalyticsUtils.sendEventWithCategoryAndValue(mTracker,
                    getString(R.string.ga_prayer_progress),
                    getString(R.string.ga_post_total_prayed_for),
                    installationId,
                    getNumPrayed());

            // TODO further analytics
            // tapped_send_message
            // tapped_add_note
            // button_press
            // share_scripture
            // tapped_view_contact
            // tapped_invite
            // tapped_show_more_people

            // categories
            // address_book_sync
            // progress_view
            // person_card_actions
            // scripture_card_action
        }
    }

    private String getString(int resId) {
        return this.activity.getString(resId);
    }

}
