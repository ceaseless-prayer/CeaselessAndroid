package org.theotech.ceaselessandroid.scripture;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class ScriptureData {
    private static final String TAG = ScriptureData.class.getSimpleName();

    private String text;
    private String citation;
    private String json;
    private String link;

    public ScriptureData(String text, String citation, String link, String json) {
        this.text = text;
        this.citation = citation;
        this.link = link;
        this.json = json;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}
