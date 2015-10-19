package org.theotech.ceaselessandroid.realm.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by uberx on 10/16/15.
 */
public class PersonPOJO implements Serializable {
    private String id;
    private String name;
    private String source;
    private List<NotePOJO> notes;
    private Date lastPrayed;
    private boolean favorite;
    private boolean ignored;
    private boolean prayed;

    public PersonPOJO(String id, String name, String source, List<NotePOJO> notes, Date lastPrayed, boolean favorite, boolean ignored, boolean prayed) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.notes = notes;
        this.lastPrayed = lastPrayed;
        this.favorite = favorite;
        this.ignored = ignored;
        this.prayed = prayed;
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

    public List<NotePOJO> getNotes() {
        return notes;
    }

    public void setNotes(List<NotePOJO> notes) {
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

    @Override
    public String toString() {
        return name;
    }
}
