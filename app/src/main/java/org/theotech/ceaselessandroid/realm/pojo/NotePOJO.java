package org.theotech.ceaselessandroid.realm.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by uberx on 10/16/15.
 */
public class NotePOJO implements Serializable {
    private String id;
    private Date creationDate;
    private Date lastUpdatedDate;
    private String title;
    private String text;
    private List<String> peopleTagged;

    public NotePOJO(String id, Date creationDate, Date lastUpdatedDate, String title, String text, List<String> peopleTagged) {
        this.id = id;
        this.creationDate = creationDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.title = title;
        this.text = text;
        this.peopleTagged = peopleTagged;
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

    public List<String> getPeopleTagged() {
        return peopleTagged;
    }

    public void setPeopleTagged(List<String> peopleTagged) {
        this.peopleTagged = peopleTagged;
    }

    @Override
    public String toString() {
        return id;
    }
}
