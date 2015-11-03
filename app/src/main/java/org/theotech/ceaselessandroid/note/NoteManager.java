package org.theotech.ceaselessandroid.note;

import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;

import java.util.List;

/**
 * Created by chrislim on 11/3/15.
 */
public interface NoteManager {

    List<NotePOJO> getNotes();

    void addNote(String title, String text);

    void editNote(String noteId, String title, String text);

    void removeNote(String noteId);

    NotePOJO getNote(String noteId);

    void tagNote(String noteId, String personId);

    void untagNote(String noteId, String personId);
}
