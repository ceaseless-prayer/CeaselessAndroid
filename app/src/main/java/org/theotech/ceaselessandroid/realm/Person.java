package org.theotech.ceaselessandroid.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ben Johnson on 10/3/15.
 */

public class Person extends RealmObject {

    @PrimaryKey
    private String id;
    private String name;
    private String source;
    private RealmList<Note> notes;
    private Date lastPrayed;
    private boolean favorite;
    private boolean ignored;
    private boolean prayed;

    public Person() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public RealmList<Note> getNotes() {
        return notes;
    }

    public void setNotes(RealmList<Note> notes) {
        this.notes = notes;
    }

    public Date getLastPrayed() {
        return lastPrayed;
    }

    public void setLastPrayed(Date lastPrayed) {
        this.lastPrayed = lastPrayed;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public boolean isPrayed() {
        return prayed;
    }

    public void setPrayed(boolean prayed) {
        this.prayed = prayed;
    }
}
