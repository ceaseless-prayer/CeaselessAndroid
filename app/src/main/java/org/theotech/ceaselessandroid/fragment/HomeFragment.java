package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.viewpagerindicator.LinePageIndicator;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.image.DownloadFileAsyncTask;
import org.theotech.ceaselessandroid.image.ImageURLService;
import org.theotech.ceaselessandroid.image.ImageURLServiceImpl;
import org.theotech.ceaselessandroid.person.AlreadyPrayedForAllContactsException;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.scripture.ScriptureService;
import org.theotech.ceaselessandroid.scripture.ScriptureServiceImpl;
import org.theotech.ceaselessandroid.transformer.ZoomOutPageTransformer;
import org.theotech.ceaselessandroid.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private final Handler handler = new Handler();

    @Bind(R.id.home_view)
    ViewPager viewPager;
    @Bind(R.id.home_indicator)
    LinePageIndicator indicator;
    @Bind(R.id.backgroundImageView)
    ImageView backgroundImageView;

    private Runnable runPager;
    private boolean mCreated = false;
    private boolean useCache;
    private FragmentStateListener mListener;
    private CacheManager cacheManager = null;
    private ImageURLService imageService = null;
    private ScriptureService scriptureService = null;
    private PersonManager personManager = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // notify fragment state
        try {
            mListener = (FragmentStateListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FragmentStateListener");
        }
        Bundle currentState = new Bundle();
        currentState.putInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG, 0);
        mListener.notify(new FragmentState(getString(R.string.nav_home), currentState));
        // use cache for easter egg
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.USE_CACHE_BUNDLE_ARG)) {
            this.useCache = bundle.getBoolean(Constants.USE_CACHE_BUNDLE_ARG);
        } else {
            this.useCache = true;
        }

        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
        imageService = ImageURLServiceImpl.getInstance();
        scriptureService = ScriptureServiceImpl.getInstance();
        personManager = PersonManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_home));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        setupBackgroundImage();

        /*
         * cache data if needed
         */

        // decide whether or not to fetch a new verse image
        String verseImageURL = cacheManager.getCachedVerseImageURL();
        if (!useCache || verseImageURL == null) {
            updateBackgroundImage();
            new ImageFetcher().execute();
        }

        // verse title and text
        ScriptureData scriptureData = cacheManager.getCachedScripture();
        if (!useCache || scriptureData == null) {
            new ScriptureFetcher().execute();
        }
        // people to pray for
        List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
        if (!useCache || personIds == null) {
            List<PersonPOJO> personPOJOs;
            try {
                personPOJOs = personManager.getNextPeopleToPrayFor(Constants.NUM_PERSONS);
            } catch (AlreadyPrayedForAllContactsException e) {
                // TODO: Celebrate!
                try {
                    personPOJOs = personManager.getNextPeopleToPrayFor(Constants.NUM_PERSONS);
                } catch (AlreadyPrayedForAllContactsException e1) {
                    // TODO: Something is really wrong if this happens, not sure what to do here
                    throw new RuntimeException(e1);
                }
            }
            personIds = new ArrayList<>();
            for (PersonPOJO personPOJO : personPOJOs) {
                personIds.add(personPOJO.getId());
            }
            cacheManager.cachePersonIdsToPrayFor(personIds);
            handler.post(runPager);
        }

        // wire up the home view pager
        runPager = new Runnable() {
            @Override
            public void run() {
                viewPager.setOffscreenPageLimit(Constants.NUM_PERSONS + 1);
                viewPager.setAdapter(new FragmentStatePagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager()) {
                    @Override
                    public android.support.v4.app.Fragment getItem(int position) {
                        android.support.v4.app.Fragment fragment;
                        Bundle bundle = new Bundle();
                        if (position == 0) {
                            fragment = new VerseCardSupportFragment();
                        } else if (position == getCount() - 1) {
                            fragment = new ProgressCardSupportFragment();
                        } else {
                            List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
                            if (personIds != null && personIds.size() > 0) {
                                String personId = personIds.get(position - 1);
                                fragment = PersonSupportFragment.newInstance(personId);
                                bundle.putInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG, position);
                            } else {
                                fragment = new BlankSupportFragment();
                            }
                        }
                        bundle.putBoolean(Constants.USE_CACHE_BUNDLE_ARG, useCache);
                        if (fragment.getArguments() != null) {
                            fragment.getArguments().putAll(bundle);
                        } else {
                            fragment.setArguments(bundle);
                        }

                        return fragment;
                    }

                    @Override
                    public int getCount() {
                        return Constants.NUM_PERSONS + 2;
                    }
                });
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        Bundle newState = new Bundle();
                        newState.putInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG, position);
                        if (position > 0 && position < Constants.NUM_PERSONS + 1) {
                            List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
                            if (personIds != null && personIds.size() > 0) {
                                String personId = personIds.get(position - 1);
                                newState.putString(Constants.PERSON_ID_BUNDLE_ARG, personId);
                            }
                        }
                        // notify fragment state
                        FragmentState fragmentState = new FragmentState(getString(R.string.nav_home), newState);
                        mListener.notify(fragmentState);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
                // wire up the indicator
                indicator.setViewPager(viewPager);
                // change the page if required
                Bundle bundle = getArguments();
                if (bundle != null && bundle.containsKey(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG)) {
                    viewPager.setCurrentItem(bundle.getInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG));
                }
            }
        };
        if (mCreated) {
            handler.post(runPager);
        }

        return view;
    }

    private void updateBackgroundImage() {
        File nextBackgroundImage = new File(getActivity().getCacheDir(), Constants.NEXT_BACKGROUND_IMAGE);
        File currentBackgroundImage = new File(getActivity().getCacheDir(), Constants.CURRENT_BACKGROUND_IMAGE);

        // move cached next background image
        if (nextBackgroundImage.exists()) {
            if (nextBackgroundImage.renameTo(currentBackgroundImage)) {
                Log.d(TAG, "Updated the background image to use from " + nextBackgroundImage + " to " + currentBackgroundImage);
                Log.d(TAG, "New image size: " + currentBackgroundImage.length());
                Picasso.with(getActivity()).invalidate(currentBackgroundImage); // clear the picasso cache
                setupBackgroundImage();
            } else {
                Log.d(TAG, "Could not update the background image to use.");
            }
        }
    }

    private void setupBackgroundImage() {
        File currentBackgroundImage = new File(getActivity().getCacheDir(), Constants.CURRENT_BACKGROUND_IMAGE);
        if (currentBackgroundImage.exists()) {
            Picasso.with(getActivity())
                    .load(currentBackgroundImage)
                    .into(backgroundImageView);
            Log.d(TAG, "Background image has been set to " + currentBackgroundImage);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (runPager != null) {
            handler.post(runPager);
        }
        mCreated = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                // cache
                cacheManager.cacheVerseImageURL(imageUrl);

                // fetch new background image
                updateBackgroundImage();
                File nextBackgroundImage = new File(getActivity().getCacheDir(), Constants.NEXT_BACKGROUND_IMAGE);
                new DownloadFileAsyncTask(getActivity(), imageUrl, nextBackgroundImage).execute();
                handler.post(runPager);
            } else {
                Log.e(TAG, "Could not fetch scripture image!");
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
                Log.d(TAG, "scripture = " + scripture);
                // cache
                cacheManager.cacheScripture(scripture);
                handler.post(runPager);
            } else {
                Log.e(TAG, "Could not fetch scripture!");
            }
        }
    }

}
