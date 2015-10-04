package org.theotech.ceaselessandroid.person;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class AlreadyPrayedForAllContactsException extends Throwable {

    long numPeople;

    public AlreadyPrayedForAllContactsException(long numPeople) {
        this.numPeople = numPeople;
    }

    public long getNumPeople() {
        return numPeople;
    }
}
