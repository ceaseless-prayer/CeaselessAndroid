package org.theotech.ceaselessandroid.person;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class AlreadyPrayedForAllContactsException extends Exception {

    long numPeople;

    public AlreadyPrayedForAllContactsException(long numPeople) {
        super("Already prayed for " + numPeople + " contacts!");
        this.numPeople = numPeople;
    }

    public long getNumPeople() {
        return numPeople;
    }
}
