package org.theotech.ceaselessandroid.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by uberx on 10/11/2015.
 */
public class Note extends RealmObject {
    @PrimaryKey
    private String id;
    private Date creationDate;
    private Date lastUpdatedDate;
    private String text;
    private RealmList<Person> peopleTagged;

    public Note() {
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
}
