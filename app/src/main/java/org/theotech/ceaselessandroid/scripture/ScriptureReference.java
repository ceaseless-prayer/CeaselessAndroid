package org.theotech.ceaselessandroid.scripture;

/**
 * Created by Ben Johnson on 10/3/15.
 */
class ScriptureReference {

    private String book;
    private String chapter;
    private String verse_start;
    private String verse_end;
    private String json;

    public ScriptureReference(String book, String chapter, String verse_start, String verse_end, String json) {
        this.book = book;
        this.chapter = chapter;
        this.verse_start = verse_start;
        this.verse_end = verse_end;
        this.json = json;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getVerse_start() {
        return verse_start;
    }

    public void setVerse_start(String verse_start) {
        this.verse_start = verse_start;
    }

    public String getVerse_end() {
        return verse_end;
    }

    public void setVerse_end(String verse_end) {
        this.verse_end = verse_end;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
