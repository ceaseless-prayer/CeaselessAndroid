package org.theotech.ceaselessandroid.util;

import org.theotech.ceaselessandroid.realm.Person;
import org.theotech.ceaselessandroid.realm.RealmString;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by uberx on 10/11/2015.
 */
public class RealmUtils {
    public static List<String> convert(RealmList<RealmString> list) {
        List<String> result = new ArrayList<String>();
        for (RealmString item : list) {
            result.add(item.getString());
        }
        return result;
    }

    public static RealmList<RealmString> convert(List<String> list) {
        RealmList<RealmString> result = new RealmList<RealmString>();
        for (String item : list) {
            result.add(new RealmString(item));
        }
        return result;
    }

    public static List<String> convertToIds(List<Person> persons) {
        List<String> personIds = new ArrayList<String>();
        for (Person person : persons) {
            personIds.add(person.getId());
        }
        return personIds;
    }
}
