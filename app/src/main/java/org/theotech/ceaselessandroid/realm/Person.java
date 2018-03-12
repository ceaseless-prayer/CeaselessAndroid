package org.theotech.ceaselessandroid.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Ben Johnson on 10/3/15.
 */

public class Person extends RealmObject {

    @PrimaryKey @Required
    private String id;
    private String name;
    private String source;
    private RealmList<Note> notes;
    private Date lastPrayed;
    private boolean favorite;
    private boolean ignored;
    private boolean prayed;
    private boolean active;
    public Person() {
    }

    public Person(String id, String name, String source, RealmList<Note> notes, Date lastPrayed,
                  boolean favorite, boolean ignored, boolean prayed, boolean active) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.notes = notes;
        this.lastPrayed = lastPrayed;
        this.favorite = favorite;
        this.ignored = ignored;
        this.prayed = prayed;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public final class Column {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String SOURCE = "source";
        public static final String NOTES = "notes";
        public static final String LAST_PRAYED = "lastPrayed";
        public static final String FAVORITE = "favorite";
        public static final String IGNORED = "ignored";
        public static final String PRAYED = "prayed";
        public static final String ACTIVE = "active";
    }
}
