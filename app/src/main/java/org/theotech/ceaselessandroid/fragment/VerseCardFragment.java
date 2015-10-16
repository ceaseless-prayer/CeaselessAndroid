package org.theotech.ceaselessandroid.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.image.ImageURLService;
import org.theotech.ceaselessandroid.image.ImageURLServiceImpl;
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.scripture.ScriptureService;
import org.theotech.ceaselessandroid.scripture.ScriptureServiceImpl;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VerseCardFragment extends Fragment {
    private static final String TAG = VerseCardFragment.class.getSimpleName();

    @Bind(R.id.verse_image)
    ImageView verseImage;
    @Bind(R.id.verse_title)
    TextView verseTitle;
    @Bind(R.id.verse_text)
    TextView verseText;

    private boolean useCache;
    private CacheManager cacheManager = null;
    private ImageURLService imageService = null;
    private ScriptureService scriptureService = null;

    public VerseCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.USE_CACHE_BUNDLE_ARG)) {
            this.useCache = bundle.getBoolean(Constants.USE_CACHE_BUNDLE_ARG);
        } else {
            this.useCache = true;
        }

        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
        imageService = ImageURLServiceImpl.getInstance();
        scriptureService = ScriptureServiceImpl.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view and bind
        View view = inflater.inflate(R.layout.fragment_verse_card, container, false);
        ButterKnife.bind(this, view);

        // verse image
        String verseImageURL = cacheManager.getCachedVerseImageURL();
        if (verseImageURL != null) {
            drawVerseImage(verseImageURL);
        }

        // verse title and text
        ScriptureData scriptureData = cacheManager.getCachedScripture();
        if (scriptureData != null) {
            populateVerse(scriptureData.getCitation(), scriptureData.getText());
        }

        return view;
    }

    private void drawVerseImage(String verseImageURL) {
        Picasso.with(getActivity()).load(verseImageURL).placeholder(R.drawable.placeholder_rectangle_scene).fit().into(verseImage);
    }

    private void populateVerse(String citation, String text) {
        verseTitle.setText(citation);
        verseText.setText(text);
    }

    private class ImageFetcher extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return imageService.getImageURL();
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (imageUrl != null) {
                Log.d(TAG, "imageUrl = " + imageUrl);
                drawVerseImage(imageUrl);
                // cache
                cacheManager.cacheVerseImageURL(imageUrl);
            } else {
                Log.e(TAG, "Could not fetch scripture!");
            }
        }
    }

    private class ScriptureFetcher extends AsyncTask<String, Void, ScriptureData> {

        @Override
        protected ScriptureData doInBackground(String... params) {
            return scriptureService.getScripture();
        }

        @Override
        protected void onPostExecute(ScriptureData scripture) {
            if (scripture != null) {
                Log.d(TAG, "scripture = " + scripture.getJson());
                populateVerse(scripture.getCitation(), scripture.getText());
                // cache
                cacheManager.cacheScripture(scripture);
            } else {
                Log.e(TAG, "Could not fetch scripture!");
                populateVerse(getString(R.string.default_verse_title), getString(R.string.default_verse_text));
            }
        }
    }

}
