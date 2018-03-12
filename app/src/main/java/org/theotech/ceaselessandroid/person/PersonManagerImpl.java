package org.theotech.ceaselessandroid.person;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;

import org.theotech.ceaselessandroid.CeaselessApplication;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.realm.Note;
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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import io.realm.Case;
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
    private static final int RANDOM_FAVORITE_THRESHOLD = 7;
    private static final int RANDOM_SAMPLE_POST_METRICS = 2;

    private static PersonManager instance;
    private Activity activity;
    private Realm realm;
    private ContentResolver contentResolver;
    private CacheManager cacheManager;
    private Tracker mTracker;

    private PersonManagerImpl(Activity activity) {
        this.activity = activity;
        this.realm = Realm.getDefaultInstance();
        this.contentResolver = this.activity.getContentResolver();
        this.cacheManager = LocalDailyCacheManagerImpl.getInstance(activity);

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
        List<PersonPOJO> peopleToPrayFor = new ArrayList<>();

        // Get all people who haven't been prayed for, sorted by last_prayed
        RealmResults<Person> results = realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, false)
                .equalTo(Person.Column.PRAYED, false)
                .sort(Person.Column.LAST_PRAYED)
                .findAll();
        handleAllPrayedFor(results); // resets all the prayed flags and
                                     // throws AlreadyPrayedForAllContactsException when needed
        // We still have people available to be prayed for
        List<Person> peopleAvailableToBePrayedFor = getShuffledListOfAllPeople(results);
        if (peopleAvailableToBePrayedFor.size() < 1) {
            return peopleToPrayFor;
        }

        // Add preselectedPerson to prayer list if available
        Person preselectedPerson = loadPreselectedPerson();
        if (preselectedPerson != null) {
            selectPerson(preselectedPerson);
            peopleToPrayFor.add(getPerson(preselectedPerson.getId()));
            peopleAvailableToBePrayedFor.remove(preselectedPerson);
        }

        // Add more people to pray for (until we run out of unprayed people or have enough)
        Integer numToSelect = Math.min(n - peopleToPrayFor.size(),
                peopleAvailableToBePrayedFor.size());
        int personIndex = 0;
        for (int i = 0; i < numToSelect; i++) {
            Person person = peopleAvailableToBePrayedFor.get(personIndex);
            // Select this person if they haven't been chosen yet
            if (!peopleToPrayFor.contains(getPerson(person.getId()))) {
                selectPerson(person);
                peopleToPrayFor.add(getPerson(person.getId()));
                peopleAvailableToBePrayedFor.remove(person);
                personIndex--;
            }
            personIndex++;
        }

        if (peopleAvailableToBePrayedFor.size() > 0) {
            preselectPerson(peopleAvailableToBePrayedFor);
        }
        return peopleToPrayFor;
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

    private void handleAllPrayedFor(RealmResults<Person> results) throws AlreadyPrayedForAllContactsException {
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
    }

    private Person selectFavoritedPerson() {
        RealmResults<Person> favoritedPeople = realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.FAVORITE, true)
                .equalTo(Person.Column.IGNORED, false)
                .sort(Person.Column.LAST_PRAYED)
                .findAll();

        if (favoritedPeople.size() > 0) {
            if (favoritedPeople.size() < RANDOM_FAVORITE_THRESHOLD) {
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
                .apply();
    }

    private Person loadPreselectedPerson() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String personId = sp.getString(Constants.PRESELECTED_PERSON_ID, null);
        if (personId == null) {
            return null;
        }
        return getRealmPerson(personId);
    }

    private void selectPerson(Person person) {
        realm.beginTransaction();
        person.setLastPrayed(new Date());
        person.setPrayed(true);
        realm.commitTransaction();
        Log.d(TAG, "Selecting person " + person);
    }

    @Override
    public List<PersonPOJO> getActivePeople() {
        return RealmUtils.toPersonPOJOs(realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, false)
                .sort(Person.Column.NAME)
                .findAll());
    }

    @Override
    public List<PersonPOJO> getFavoritePeople() {
        return RealmUtils.toPersonPOJOs(realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.FAVORITE, true)
                .equalTo(Person.Column.IGNORED, false)
                .sort(Person.Column.NAME)
                .findAll());
    }

    @Override
    public List<PersonPOJO> getRemovedPeople() {
        return RealmUtils.toPersonPOJOs(realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .equalTo(Person.Column.IGNORED, true)
                .sort(Person.Column.NAME)
                .findAll());
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
        List<String> ids = new ArrayList<>();
        List<Person> people = new ArrayList<>();

        try {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            while (cursor.moveToNext()) {
                if (isValidContact(cursor)) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    ids.add(id);
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.v(TAG, String.format("Person: id=%s name=%s", id, name));

                    Person person = realm.where(Person.class)
                            .equalTo(Person.Column.ID, id)
                            .findFirst();

                    if (person == null) {

                        person = realm.createObject(Person.class, id);
                        person.setName(name);
                        person.setSource(CONTACTS_SOURCE);
                        person.setActive(true);
                        person.setIgnored(false);
                        person.setLastPrayed(new Date(0L));

                        // auto-favorite contacts that were starred by user.
                        boolean starred = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.STARRED)) == 1;
                        person.setFavorite(starred);

                        ++added;
                    } else {
                        Log.v(TAG, "User already existed, updating information.");
                        // Update the name in case the user updates it
                        person.setName(name);
                        person.setActive(true);
                        ++updated;
                    }
                    people.add(person);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Clean up contacts whose ids have now changed
        // Copy their contents to an existing contact if there is a matching one.
        RealmResults<Person> fullList = realm.where(Person.class)
                .equalTo(Person.Column.ACTIVE, true)
                .findAll();

        List<Pair<String, Person>> peopleWithUpdatedIds = new ArrayList<>();

        for (int i = 0; i < fullList.size(); i++) {
            Person p = fullList.get(i);
            if (!ids.contains(p.getId())) {
                Log.v(TAG, "Contact with id " + p.getId() + " no longer exists.");
                Person matchingContact = findPersonWithName(p.getName(), people);
                if (matchingContact != null) {
                    Log.v(TAG, "A contact with name " + p.getName() + " exists. Merging details.");
                    copyContact(p, matchingContact);
                    // update daily cache with new ids if any persons selected for today are affected
                    peopleWithUpdatedIds.add(Pair.create(p.getId(), matchingContact));
                    // cleanup the obsolete contact (must be after we've update the cache)
                    p.deleteFromRealm();
                } else {
                    Log.v(TAG, "Marking this contact inactive because it no longer exists on the phone.");
                    p.setActive(false);
                    peopleWithUpdatedIds.add(Pair.create(p.getId(), (Person) null));
                }
            }
        }

        realm.commitTransaction();

        // we need to update the cache in a separate transaction
        for (Pair<String, Person> p : peopleWithUpdatedIds) {
            updateCachedPersonIds(p.first, p.second);
        }

        Log.d(TAG, String.format("Successfully added %d and updated %d contacts.", added, updated));
        sampleAndPostMetrics();
    }

    private void updateCachedPersonIds(String oldContactId, Person newContact) {
        List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
        if (personIds != null && personIds.contains(oldContactId)) {
            Log.i(TAG, "Updating a selected contact in daily cache.");
            personIds.remove(oldContactId);
            if (newContact != null) {
                personIds.add(newContact.getId());
            }
            cacheManager.cachePersonIdsToPrayFor(personIds);
        }
    }

    private Person findPersonWithName(String name, List<Person> people) {
        for (Person p : people) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    private void copyContact(Person src, Person dst) {
        // do not copy id or name.
        // copy over everything else
        dst.setActive(src.isActive());
        dst.setFavorite(src.isFavorite());
        dst.setIgnored(src.isIgnored());
        dst.setLastPrayed(src.getLastPrayed());
        dst.setPrayed(src.isPrayed());
        dst.setSource(src.getSource());

        transferNotes(src, dst);

        // TODO potentially update the Cache
        // so all ids that pointed to the old contact, point to the new.
    }

    private void transferNotes(Person src, Person dst) {
        // transfer notes over
        RealmList<Note> notes = src.getNotes();
        for (Note n : notes) {
            RealmList<Person> peopleTagged = n.getPeopleTagged();
            RealmList<Person> newPeopleTagged = new RealmList<>();
            Iterator<Person> i = peopleTagged.iterator();
            // remove the old version of this person on the note
            while (i.hasNext()) {
                Person p = i.next();
                if (p.getId().equals(src.getId())) {
                    // add the new version of this person on the note
                    newPeopleTagged.add(dst);
                } else {
                    newPeopleTagged.add(p);
                }
            }
            n.setPeopleTagged(newPeopleTagged);
        }
        dst.setNotes(src.getNotes());
    }

    @Override
    public List<PersonPOJO> queryPeopleByName(String query) {
        List<PersonPOJO> people = RealmUtils.toPersonPOJOs(realm.where(Person.class)
                .contains(Person.Column.NAME, query, Case.INSENSITIVE)
                .equalTo(Person.Column.ACTIVE, true)
                .sort(Person.Column.NAME)
                .findAll());
        return people;
    }

    private boolean isValidContact(Cursor cursor) {
        boolean hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1;
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        return hasPhoneNumber && !name.startsWith("#") && !name.startsWith("+") && contactNameFilter(name);
    }

    private boolean contactNameFilter(String name) {

        if (name == null || name.isEmpty()) {
            return false;
        }

        if (Character.isDigit(name.charAt(0))) {
            // if the name starts with a number ignore it.
            return false;
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(name).matches()) {
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
