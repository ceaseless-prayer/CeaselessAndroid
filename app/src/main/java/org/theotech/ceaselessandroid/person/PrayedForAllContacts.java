package org.theotech.ceaselessandroid.person;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class PrayedForAllContacts extends Throwable {

    public long getNumPeople() {
        return numPeople;
    }

    long numPeople;

    public PrayedForAllContacts(long numPeople) {
        this.numPeople = numPeople;
    }
}
