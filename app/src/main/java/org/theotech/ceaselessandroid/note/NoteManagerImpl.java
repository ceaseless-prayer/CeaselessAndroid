package org.theotech.ceaselessandroid.note;

import android.app.Activity;
import android.content.ContentResolver;

import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.Note;
import org.theotech.ceaselessandroid.realm.Person;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.RealmUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by chrislim on 11/3/15.
 */
public class NoteManagerImpl implements NoteManager {
    private static final String TAG = NoteManagerImpl.class.getSimpleName();
    private static NoteManager instance;
    private Activity activity;
    private Realm realm;
    private ContentResolver contentResolver;

    private NoteManagerImpl(Activity activity) {
        this.activity = activity;
        this.realm = Realm.getDefaultInstance();
        this.contentResolver = activity.getContentResolver();
    }

    public static NoteManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new NoteManagerImpl(activity);
        }
        return instance;
    }

    @Override
    public List<NotePOJO> getNotes() {
        return RealmUtils.toNotePOJOs(realm.where(Note.class)
                .equalTo(Note.Column.ACTIVE, true)
                .findAllSorted(Note.Column.LAST_UPDATED_DATE));
    }

    @Override
    public void addNote(String title, String text, List<PersonPOJO> personPOJOs) {
        realm.beginTransaction();
        Note note = realm.createObject(Note.class);
        note.setCreationDate(new Date());
        note.setLastUpdatedDate(new Date());
        note.setId(UUID.randomUUID().toString());
        note.setActive(true);
        if (title != null) {
            note.setTitle(title);
        }
        note.setText(text);
        PersonManager pm = PersonManagerImpl.getInstance(activity);
        RealmList<Person> people = pm.getPersonFromPersonPOJO(personPOJOs);
        note.setPeopleTagged(people);
        for (PersonPOJO p : personPOJOs) {
            pm.getRealmPerson(p.getId()).getNotes().add(note);
        }
        realm.commitTransaction();
    }

    @Override
    public void editNote(String noteId, String title, String text, List<PersonPOJO> personPOJOs) {
        realm.beginTransaction();
        Note note = getRealmNote(noteId);
        note.setLastUpdatedDate(new Date());
        note.setTitle(title);
        note.setText(text);
        note.setActive(true);

        // we need to cleanup people no longer tagged and add people who are new
        PersonManager pm = PersonManagerImpl.getInstance(activity);
        RealmList<Person> oldPeopleTagged = note.getPeopleTagged();
        for (Person p : oldPeopleTagged) {
            p.getNotes().remove(note);
        }

        // tag the new people
        RealmList<Person> people = pm.getPersonFromPersonPOJO(personPOJOs);
        note.setPeopleTagged(people);
        for (PersonPOJO p : personPOJOs) {
            pm.getRealmPerson(p.getId()).getNotes().add(note);
        }

        realm.commitTransaction();
    }

    @Override
    public void removeNote(String noteId) {
        realm.beginTransaction();
        getRealmNote(noteId).removeFromRealm();
        realm.commitTransaction();
    }

    @Override
    public NotePOJO getNote(String noteId) {
        return RealmUtils.toNotePOJO(getRealmNote(noteId));
    }

    private Note getRealmNote(String noteId) {
        return realm.where(Note.class)
                .equalTo(Note.Column.ID, noteId)
                .findFirst();
    }

    @Override
    public void tagNote(String noteId, String personId) {
        // TODO: Implement
    }

    @Override
    public void untagNote(String noteId, String personId) {
        // TODO: Implement
    }

    @Override
    public List<NotePOJO> queryNotesByText(String query) {
        List<Note> results = realm.where(Note.class)
                .equalTo(Note.Column.ACTIVE, true)
                .contains(Note.Column.TEXT, query, false)
                .findAllSorted(Note.Column.LAST_UPDATED_DATE);
        return RealmUtils.toNotePOJOs(results);
    }
}
