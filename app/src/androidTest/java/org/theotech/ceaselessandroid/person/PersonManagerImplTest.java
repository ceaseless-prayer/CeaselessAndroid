//package org.theotech.ceaselessandroid.person;
//
//import android.content.Context;
//import android.test.mock.MockContext;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
///**
// * Created by Ben Johnson on 10/3/15.
// */
//public class PersonManagerImplTest extends TestCase{
//
//    PersonManager manager;
//    Context context;
//
//    @Before
//    public void setUp(){
//        context = new MockContext();
//        manager = PersonManagerImpl.getInstance(context);
//    }
//
//    @Test
//    public void testGetNextPeopleToPrayFor() throws Exception {
//
//    }
//
//    @Test
//    public void testGetAllPeople() throws Exception {
//
//    }
//
//    @Test
//    public void testGetPerson() throws Exception {
//
//    }
//
//    @Test
//    public void testRemovePerson() throws Exception {
//
//    }
//
//    @Test
//    public void testFavoritePerson() throws Exception {
//
//    }
//
//    @Test
//    public void testUnfavoritePerson() throws Exception {
//
//    }
//
//    @Test
//    public void testPopulateContacts() throws Exception {
//
//    }
//}

//        String testperson = "1951";
//        Log.d(TAG, String.format("person 1951 = '%s'", personToString(personManager.getPerson(testperson))));
//        personManager.favoritePerson(testperson);
//        Log.d(TAG, String.format("person 1951 = '%s'", personToString(personManager.getPerson(testperson))));
//        personManager.unfavoritePerson(testperson);
//        Log.d(TAG, String.format("person 1951 = '%s'", personToString(personManager.getPerson(testperson))));
//        personManager.ignorePerson(testperson);
//        Log.d(TAG, String.format("person 1951 = '%s'", personToString(personManager.getPerson(testperson))));
//        personManager.unignorePerson(testperson);
//        Log.d(TAG, String.format("person 1951 = '%s'", personToString(personManager.getPerson(testperson))));
//
//        Log.d(TAG, String.format("getNumPeople = '%d'", personManager.getNumPeople()));
//        Log.d(TAG, String.format("getNumPrayed = '%d'", personManager.getNumPrayed()));
//        try {
//        for (Person p : personManager.getNextPeopleToPrayFor(3)) {
//        Log.d(TAG, String.format("prayed for person = '%s'", personToString(p)));
//        }
//        } catch (AlreadyPrayedForAllContactsException e) {
//        Log.e(TAG, "You've prayed for all your contacts! Hallelujah!");
//        }
//        Log.d(TAG, String.format("getNumPrayed = '%d'", personManager.getNumPrayed()));
//
//static private TimeZone tz = TimeZone.getTimeZone("UTC");
//static private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
//public String personToString(Person person){
//        StringBuilder sb = new StringBuilder();
//        sb.append("{\"id\":\"").append(person.getId()).append("\",");
//        sb.append("\"name\":\"").append(person.getName()).append("\",");
//        sb.append("\"source\":\"").append(person.getSource()).append("\",");
//        sb.append("\"lastPrayed\":\"").append(df.format(person.getLastPrayed())).append("\",");
//        sb.append("\"favorite\":").append(person.isFavorite()?"true":"false").append(",");
//        sb.append("\"ignored\":").append(person.isIgnored()?"true":"false").append(",");
//        sb.append("\"prayed\":").append(person.isFavorite()?"true":"false").append("}");
//        return sb.toString();
//        }