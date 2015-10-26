package org.theotech.ceaselessandroid.util;

import org.theotech.ceaselessandroid.realm.LocalCacheData;
import org.theotech.ceaselessandroid.realm.Note;
import org.theotech.ceaselessandroid.realm.Person;
import org.theotech.ceaselessandroid.realm.RealmString;
import org.theotech.ceaselessandroid.realm.pojo.LocalCacheDataPOJO;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by uberx on 10/11/2015.
 */
public class RealmUtils {

    public static List<String> convert(RealmList<RealmString> list) {
        List<String> result = new ArrayList<>();
        for (RealmString item : list) {
            result.add(item.getString());
        }
        return result;
    }

    public static NotePOJO toNotePOJO(Note note) {
        if (note != null) {
            return new NotePOJO(note.getId(), note.getCreationDate(), note.getLastUpdatedDate(),
                    note.getTitle(), note.getText(), convert(note.getPeopleTagged()));
        }
        return null;
    }

    public static List<NotePOJO> toNotePOJOs(List<Note> notes) {
        if (notes != null) {
            List<NotePOJO> result = new ArrayList<>();
            for (Note note : notes) {
                result.add(toNotePOJO(note));
            }
            return result;
        }
        return null;
    }

    public static PersonPOJO toPersonPOJO(Person person) {
        if (person != null) {
            return new PersonPOJO(person.getId(), person.getName(), person.getSource(),
                    toNotePOJOs(person.getNotes()), person.getLastPrayed(), person.isFavorite(),
                    person.isIgnored(), person.isPrayed());
        }
        return null;
    }

    public static List<PersonPOJO> toPersonPOJOs(List<Person> persons) {
        if (persons != null) {
            List<PersonPOJO> result = new ArrayList<>();
            for (Person person : persons) {
                result.add(toPersonPOJO(person));
            }
            return result;
        }
        return null;
    }

    public static LocalCacheDataPOJO toLocalCacheDataPOJO(LocalCacheData localCacheData) {
        if (localCacheData != null) {
            return new LocalCacheDataPOJO(localCacheData.getCreationDate(),
                    localCacheData.getScriptureCitation(), localCacheData.getScriptureText(),
                    localCacheData.getScriptureJson(), localCacheData.getVerseImageURL(),
                    convert(localCacheData.getPersonIdsToPrayFor()));
        }
        return null;
    }
}
