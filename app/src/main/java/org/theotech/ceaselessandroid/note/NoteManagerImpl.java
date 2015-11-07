package org.theotech.ceaselessandroid.note;

import android.content.ContentResolver;
import android.content.Context;

import org.theotech.ceaselessandroid.realm.Note;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.util.RealmUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by chrislim on 11/3/15.
 */
public class NoteManagerImpl implements NoteManager {
    private static final String TAG = NoteManagerImpl.class.getSimpleName();
    private static NoteManager instance;
    private Context context;
    private Realm realm;
    private ContentResolver contentResolver;

    private NoteManagerImpl(Context context) {
        this.context = context;
        // TODO this is also in the PersonManager, but maybe it should be in one place?
        RealmConfiguration config = new RealmConfiguration.Builder(context)
                .name("org.theotech.ceaselessandroid")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        this.realm = Realm.getDefaultInstance();
        this.contentResolver = context.getContentResolver();
    }

    public static NoteManager getInstance(Context context) {
        if (instance == null) {
            instance = new NoteManagerImpl(context);
        }
        return instance;
    }

    @Override
    public List<NotePOJO> getNotes() {
        return RealmUtils.toNotePOJOs(realm.where(Note.class).findAllSorted("lastUpdatedDate"));
    }

    @Override
    public void addNote(String title, String text) {
        realm.beginTransaction();
        Note note = realm.createObject(Note.class);
        note.setCreationDate(new Date());
        note.setLastUpdatedDate(new Date());
        note.setId(UUID.randomUUID().toString());
        if (title != null)
            note.setTitle(title);
        note.setText(text);
        realm.commitTransaction();
    }

    @Override
    public void editNote(String noteId, String title, String text) {
        realm.beginTransaction();
        Note note = getRealmNote(noteId);
        note.setLastUpdatedDate(new Date());
        note.setTitle(title);
        note.setText(text);
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
        return realm.where(Note.class).equalTo("id", noteId).findFirst();
    }

    @Override
    public void tagNote(String noteId, String personId) {
        // TODO: Implement
    }

    @Override
    public void untagNote(String noteId, String personId) {
        // TODO: Implement
    }
}
