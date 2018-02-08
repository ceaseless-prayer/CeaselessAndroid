package org.theotech.ceaselessandroid.scripture;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ben Johnson on 10/3/15.
 */
public class ScriptureServiceImpl implements ScriptureService {

    private static final String TAG = "ScriptureServiceImpl";

    private static final String HTTP_API_VOTD = "http://api.ceaselessprayer.com/v1/votd";
    private static final String HTTP_API_GET_SCRIPTURE = "http://api.ceaselessprayer.com/v1/getScripture";
    private static final Integer SCRIPTURE_CACHE_SIZE = 5;
    private static final String SCRIPTURE_CACHE_FILE = "scriptureCacheFile";

    private static ScriptureService instance;

    protected Activity activity;
    private List<ScriptureData> cachedScriptures;

    public static ScriptureService getInstance(Activity activity) {
        if (instance == null) {
            instance = new ScriptureServiceImpl(activity);
        }
        return instance;
    }

    public ScriptureServiceImpl(Activity activity) {
        this.activity = activity;
        this.cachedScriptures = loadCacheFromFile();
    }

    @Override
    public ScriptureData getScripture() {
        return popScripture();
    }

    protected ScriptureData popScripture() {
        ScriptureData result = null;
        if (cachedScriptures.size() > 0) {
            result = cachedScriptures.remove(0);
        }
        new ScriptureCacher().execute();
        return result;
    }

    protected synchronized void fillScriptureCache() {
        while (cachedScriptures.size() < SCRIPTURE_CACHE_SIZE) {
            cachedScriptures.add(loadNewScripture());
        }
        writeCacheToFile();
    }

    private void writeCacheToFile() {
        ObjectOutputStream oos = null;
        try {
            File scriptureCacheFile = new File(activity.getCacheDir(), SCRIPTURE_CACHE_FILE);
            FileOutputStream fout = new FileOutputStream(scriptureCacheFile);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(cachedScriptures);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<ScriptureData> loadCacheFromFile() {
        File scriptureCacheFile = new File(activity.getCacheDir(), SCRIPTURE_CACHE_FILE);

        if (scriptureCacheFile.exists()) {
            Log.d(TAG, "Loading scripture cache file");
            ObjectInputStream ois = null;
            try {
                FileInputStream fin = new FileInputStream(scriptureCacheFile);
                ois = new ObjectInputStream(fin);
                List<ScriptureData> scriptureCache = (List<ScriptureData>) ois.readObject();
                ois.close();
                return scriptureCache;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // default to empty list if we fail to load the file
        return new ArrayList<>();
    }

    protected ScriptureData loadNewScripture() {
        ScriptureReference ref = getVerseReference();
        ScriptureData data = null;
        if (ref != null) {
            data = getScriptureData(ref);
        }
        return data;
    }

    protected ScriptureReference getVerseReference() {
        // HTTP Get
        InputStream in;
        try {
            URL url = new URL(HTTP_API_VOTD + "?language=" + Locale.getDefault().toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }

        String json = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.w(TAG, Log.getStackTraceString(e));
                }
            }
        }

        // parse JSON response
        ScriptureReference ref;
        try {
            Log.d(TAG, "json = " + json);
            JSONObject obj = new JSONObject(json);
            String book = obj.getString("book");
            String chapter = obj.getString("chapter");
            String verse_start = obj.getString("verse_start");
            String verse_end = obj.getString("verse_end");
            ref = new ScriptureReference(book, chapter, verse_start, verse_end, obj.toString());
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }

        return ref;
    }

    private ScriptureData getScriptureData(ScriptureReference ref) {
        // HTTP Post
        InputStream in;
        try {
            JSONObject json = new JSONObject(ref.getJson());
            json.put("language", Locale.getDefault().toString());
            Log.d(TAG, json.toString());
            byte[] postDataBytes = json.toString().getBytes("UTF-8");
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
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }

        String json = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.w(TAG, Log.getStackTraceString(e));
                }
            }
        }

        // Parse JSON response
        ScriptureData data;
        try {
            JSONObject obj = new JSONObject(json);
            String text = obj.getString("text");
            String citation = obj.getString("citation");
            String bible = obj.getString("bible");
            JSONObject refObj = new JSONObject(ref.getJson());
            String link = String.format("%s/%s/%s/%s#%s", "http://www.bible.is", bible, refObj.getString("book"), refObj.getString("chapter"), refObj.getString("verse_start"));
            Log.d(TAG, link);
            data = new ScriptureData(text, citation, link, json);
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
        return data;
    }

    private class ScriptureCacher extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            fillScriptureCache();
            return null;
        }
    }
}
