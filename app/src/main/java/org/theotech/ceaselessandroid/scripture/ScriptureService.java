package org.theotech.ceaselessandroid.scripture;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class ScriptureService implements ScriptureServiceInterface {

    private static final String TAG = "ScriptureService";

    private static final String HTTP_API_VOTD = "http://api.ceaselessprayer.com/v1/votd";
    private static final String HTTP_API_GET_SCRIPTURE = "http://api.ceaselessprayer.com/v1/getScripture";

    @Override
    public ScriptureData getScripture() {
        ScriptureReference ref = getVerseOfTheDay();
        ScriptureData data = null;
        if (ref != null) {
            data = getScriptureData(ref);
        }
        return data;
    }

    protected ScriptureReference getVerseOfTheDay() {

        InputStream in = null;

        // HTTP Get
        try {
            URL url = new URL(HTTP_API_VOTD);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            Log.e(TAG, "ERROR", e);
            Log.e(TAG, e.getStackTrace().toString());
            return null;
        }

        String json = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {sb.append(line);}
            json = sb.toString();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getStackTrace().toString());
            return null;
        }
        ScriptureReference ref = null;

        // parse JSON response
        try {
            Log.d(TAG, "json = " + json.toString());
            JSONObject obj = new JSONObject(json);
            String book = obj.getString("book");
            String chapter = obj.getString("chapter");
            String verse_start = obj.getString("verse_start");
            String verse_end = obj.getString("verse_end");
            ref = new ScriptureReference(book, chapter, verse_start, verse_end, obj.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getStackTrace().toString());
            return null;
        }

        return ref;
    }

    private ScriptureData getScriptureData(ScriptureReference ref) {

        InputStream in = null;

        // HTTP Post
        try {
            byte[] postDataBytes = ref.getJson().getBytes("UTF-8");
            URL url = new URL(HTTP_API_GET_SCRIPTURE);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setUseCaches(false);
            conn.getOutputStream().write(postDataBytes);
            in = new BufferedInputStream(conn.getInputStream());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getStackTrace().toString());
            return null;
        }

        String json = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {sb.append(line);}
            json = sb.toString();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, e.getStackTrace().toString());
            return null;
        }

        ScriptureData data = null;

        // Parse JSON response
        try {
            JSONObject obj = new JSONObject(json);
            String text = obj.getString("text");
            String citation = obj.getString("citation");
            data = new ScriptureData(text, citation, json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }
}
