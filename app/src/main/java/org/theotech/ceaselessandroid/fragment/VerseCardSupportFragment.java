package org.theotech.ceaselessandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.util.Constants;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.blurry.Blurry;

public class VerseCardSupportFragment extends Fragment {
    private static final String TAG = VerseCardSupportFragment.class.getSimpleName();

    @Bind(R.id.verse_image)
    ImageView verseImage;
    @Bind(R.id.verse_image_reflection)
    ImageView verseImageReflection;
    @Bind(R.id.verse_text_container)
    RelativeLayout verseTextContainer;
    @Bind(R.id.verse_title)
    TextView verseTitle;
    @Bind(R.id.verse_text)
    TextView verseText;
    @Bind(R.id.verse_share)
    IconTextView verseShare;

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
        final ScriptureData scriptureData = cacheManager.getCachedScripture();
        Log.d(TAG, "Scripture Data" + scriptureData);
        if (scriptureData != null) {
            populateVerse(scriptureData.getCitation(), scriptureData.getText());
        }

        verseShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = scriptureData.getText() + " " + scriptureData.getCitation();
                String shareLink = scriptureData.getLink();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

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

            Picasso.with(getActivity()).load(currentBackgroundImage)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .rotate(180f)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.placeholder_rectangle_scene)
                    .into(verseImageReflection, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Blurry.with(getActivity()).radius(10).color(R.color.verseBackground).sampling(4).capture(verseImageReflection).into(verseImageReflection);
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "hit an error");
                        }
                    });

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
