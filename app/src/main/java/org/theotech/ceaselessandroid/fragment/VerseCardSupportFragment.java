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
import com.squareup.picasso.Transformation;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class VerseCardSupportFragment extends Fragment implements ICardPageFragment {
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
                String shareLink = scriptureData.getLink();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        return view;
    }

    private void drawVerseImage() {
        File currentBackgroundImage = new File(getActivity().getCacheDir(), Constants.CURRENT_BACKGROUND_IMAGE);
        List<Transformation> transformations = new ArrayList<>();
        transformations.add(new BlurTransformation(getActivity(), 25, 4));

        if (currentBackgroundImage.exists()) {
            Log.d(TAG, "Showing verse image");

            Picasso.with(getActivity()).load(currentBackgroundImage)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .fit()
                    .centerCrop()
                    .into(verseImage);

            Picasso.with(getActivity()).load(currentBackgroundImage)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .fit()
                    .centerCrop()
                    .transform(transformations)
                    .into(verseImageReflection);
        } else {
            Log.d(TAG, "Showing default verse image");
            Picasso.with(getActivity()).load(R.drawable.at_the_beach)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .fit()
                    .centerCrop()
                    .into(verseImage);

            Picasso.with(getActivity()).load(R.drawable.at_the_beach)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .fit()
                    .centerCrop()
                    .transform(transformations)
                    .into(verseImageReflection);
        }
    }

    private void populateVerse(String citation, String text) {
        verseTitle.setText(citation);
        verseText.setText(text);
    }

    @Override
    public String getCardName() {
        return "VerseCard";
    }
}
