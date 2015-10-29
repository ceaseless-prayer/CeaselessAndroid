package org.theotech.ceaselessandroid.fragment;

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
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.util.Constants;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VerseCardSupportFragment extends Fragment {
    private static final String TAG = VerseCardSupportFragment.class.getSimpleName();

    @Bind(R.id.verse_image)
    ImageView verseImage;
    @Bind(R.id.verse_title)
    TextView verseTitle;
    @Bind(R.id.verse_text)
    TextView verseText;

    private CacheManager cacheManager = null;

    public VerseCardSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view and bind
        View view = inflater.inflate(R.layout.fragment_support_verse_card, container, false);
        ButterKnife.bind(this, view);

        drawVerseImage();

        // verse title and text
        ScriptureData scriptureData = cacheManager.getCachedScripture();
        if (scriptureData != null) {
            populateVerse(scriptureData.getCitation(), scriptureData.getText());
        }

        return view;
    }

    private void drawVerseImage() {
        File currentBackgroundImage = new File(getActivity().getCacheDir(), Constants.CURRENT_BACKGROUND_IMAGE);
        if (currentBackgroundImage.exists()) {
            Log.d(TAG, "Showing verse image");

            Picasso.with(getActivity()).load(currentBackgroundImage)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .fit()
                    .centerCrop()
                    .into(verseImage);
        } else {
            Log.d(TAG, "Showing default verse image");
            Picasso.with(getActivity()).load(R.drawable.at_the_beach)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .fit()
                    .centerCrop()
                    .into(verseImage);
        }
    }

    private void populateVerse(String citation, String text) {
        verseTitle.setText(citation);
        verseText.setText(text);
    }

}
