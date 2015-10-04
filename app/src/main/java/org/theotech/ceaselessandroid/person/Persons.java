package org.theotech.ceaselessandroid.person;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by uberx on 10/4/15.
 */
public class Persons {
    public static RealmList<Person> convert(List<Person> persons) {
        RealmList<Person> result = new RealmList<Person>();
        for (Person person : persons) {
            result.add(person);
        }

        return result;
    }

    public static List<Person> convert(RealmList<Person> persons) {
        List<Person> result = new ArrayList<Person>();
        for (Person person : persons) {
            result.add(person);
        }

        return result;
    }
}
