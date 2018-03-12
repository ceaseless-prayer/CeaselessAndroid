package org.theotech.ceaselessandroid.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by uberx on 10/11/2015.
 */
public class Note extends RealmObject {

    @PrimaryKey @Required
    private String id;
    private Date creationDate;
    private Date lastUpdatedDate;
    private String title;
    private String text;
    private RealmList<Person> peopleTagged;
    private boolean active;
    public Note() {
    }

    public Note(String id, Date creationDate, Date lastUpdatedDate, String title, String text,
                RealmList<Person> peopleTagged, boolean active) {
        this.id = id;
        this.creationDate = creationDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.title = title;
        this.text = text;
        this.peopleTagged = peopleTagged;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RealmList<Person> getPeopleTagged() {
        return peopleTagged;
    }

    public void setPeopleTagged(RealmList<Person> peopleTagged) {
        this.peopleTagged = peopleTagged;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public final class Column {
        public static final String ID = "id";
        public static final String CREATION_DATE = "creationDate";
        public static final String LAST_UPDATED_DATE = "lastUpdatedDate";
        public static final String TITLE = "title";
        public static final String TEXT = "text";
        public static final String PEOPLE_TAGGED = "peopleTagged";
        public static final String ACTIVE = "active";
    }
}
