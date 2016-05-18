package org.theotech.ceaselessandroid.person;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class PersonManagerImpl implements PersonManager {
    private static final String TAG = PersonManagerImpl.class.getSimpleName();
    private static final String CONTACTS_SOURCE = "Contacts";
    private static final int RANDOM_FAVORITE_THRESHOLD = 5;
    private static final int RANDOM_SAMPLE_POST_METRICS = 2;

    private static PersonManager instance;
    private Activity activity;
    private Realm realm;
    private ContentResolver contentResolver;
    private Tracker mTracker;

    private PersonManagerImpl(Activity activity) {
        this.activity = activity;
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

        handleAllPrayedFor(results, n);

        List<Person> allPeople = getShuffledListOfAllPeople(results);
        if (allPeople.size() < 1) {
            return people;
        }

        Person preselectedPerson = loadPreselectedPerson();
        if (preselectedPerson != null) {
            selectPerson(preselectedPerson, people);
            allPeople.remove(preselectedPerson);
        }

        // select the desired number of people.
        // Exclude anyone already who has already been selected from being chosen again
        Integer numToSelect = Math.min(n, allPeople.size());
        Log.d(TAG, "allPeople size = " + allPeople.size() + " numToSelect = " + numToSelect);
        int i = 0;
        while (people.size() < numToSelect) {
            Person person = allPeople.get(i);
            if (!people.contains(getPerson(person.getId()))) {
                // select this person if they haven't been chosen yet
                selectPerson(person, people);
                allPeople.remove(person);
                i--;
            }
            i++;
        }

        if (allPeople.size() > 0) {
            preselectPerson(allPeople);
        }
        return people;
    }

    @NonNull
    private List<Person> getShuffledListOfAllPeople(RealmResults<Person> results) {
        // shuffle the list of people
        List<Person> allPeople = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            allPeople.add(results.get(i));
        }
        Collections.shuffle(allPeople);
        return allPeople;
    }

    private void handleAllPrayedFor(RealmResults<Person> results, int n) throws AlreadyPrayedForAllContactsException {
        // if all people are prayed for, then reset and throw exception
        if (getNumPeople() > 0 && results.size() < n) {
            // we can't check just for result size

            // Reset prayed to false for each person that had the prayed flag set
            realm.beginTransaction();
            RealmResults<Person> resultsToReset = realm.where(Person.class)
                    .equalTo(Person.Column.ACTIVE, true)
                    .equalTo(Person.Column.IGNORED, false)
                    .equalTo(Person.Column.PRAYED, true)
                    .findAll();
            for (int i = 0; i < resultsToReset.size(); i++) {
                resultsToReset.get(i).setPrayed(false);
            }
            realm.commitTransaction();
            // Throw Exception
            throw new AlreadyPrayedForAllContactsException(getNumPeople());
        }
    }

    private Person selectFavoritedPerson() {
        RealmResults<Person> favoritedPeople = realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.FAVORITE, true)
                .findAllSorted(Person.Column.LAST_PRAYED);

        if (favoritedPeople.size() > 0) {
            if (favoritedPeople.size() < RANDOM_FAVORITE_THRESHOLD) {
                // 1/4 chance of getting a favorited person
                Random random = new Random();
                if (random.nextInt(RANDOM_FAVORITE_THRESHOLD) == 0) {
                    return favoritedPeople.get(0);
                }
            } else {
                // always show a favorited person if they have
                // favorited more than 7 people
                return favoritedPeople.get(0);
            }
        }

        return null;
    }

    private void preselectPerson(List<Person> allPeople) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        Person theChosenOne = selectFavoritedPerson();
        if (theChosenOne == null) {
            theChosenOne = allPeople.get(0);
        }
        sp.edit()
                .putString(Constants.PRESELECTED_PERSON_ID, theChosenOne.getId())
                .putString(Constants.PRESELECTED_PERSON_NAME, theChosenOne.getName())
                .commit();
    }

    private Person loadPreselectedPerson() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String personId = sp.getString(Constants.PRESELECTED_PERSON_ID, null);
        if (personId == null) {
            return null;
        }
        return getRealmPerson(personId);
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

    @Override
    public List<PersonPOJO> queryPeopleByName(String query) {
        List<PersonPOJO> people = RealmUtils.toPersonPOJOs(realm.where(Person.class)
                .contains(Person.Column.NAME, query, false)
                .equalTo(Person.Column.ACTIVE, true)
                .findAllSorted(Person.Column.NAME));
        return people;
    }

    private boolean isValidContact(Cursor cursor) {
        boolean hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1;
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        // TODO: filter out "Conference Bridge" and "Directory Assistance" as other common things to ignore
        return hasPhoneNumber && !name.startsWith("#") && !name.startsWith("+") && contactNameFilter(name);
    }

    private boolean contactNameFilter(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        List<String> blacklist = Arrays.asList(activity.getResources().getStringArray(R.array.contact_name_blacklist));
        for (String s : blacklist) {
            // match the name to the blacklist elements
            // since the argument to matches is a regular expression
            // note that we can also use Pattern.quote to not treat it as a regular expression.
            // we do it this way since in the future our blacklist may want to consist in regular expressions
            if (name.matches(s)) {
                return false;
            }
        }
        return true;
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
        }
    }

    private String getString(int resId) {
        return this.activity.getString(resId);
    }

}
