package org.theotech.ceaselessandroid.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.theotech.ceaselessandroid.CeaselessApplication;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
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
import org.theotech.ceaselessandroid.util.AnalyticsUtils;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.home_view)
    ViewPager viewPager;
    @BindView(R.id.home_indicator)
    CirclePageIndicator indicator;

    private MainActivity activity;
    private boolean useCache;
    private FragmentStateListener mListener;
    private CacheManager cacheManager = null;
    private ImageURLService imageService = null;
    private ScriptureService scriptureService = null;
    private PersonManager personManager = null;
    private Tracker mTracker;

    private SearchView searchView;
    private Integer numberOfPeopleToPrayForDaily;

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

        try {
            activity = (MainActivity) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must be an instanceof MainActivity");
        }

        Bundle currentState = new Bundle();
        currentState.putInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG, 0);
        mListener.notify(new FragmentState(getString(R.string.nav_home), currentState));

        // do not use the cache if we're trying to get more people.
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.USE_CACHE_BUNDLE_ARG)) {
            this.useCache = bundle.getBoolean(Constants.USE_CACHE_BUNDLE_ARG);
        } else {
            this.useCache = true;
        }

        cacheManager = LocalDailyCacheManagerImpl.getInstance(activity);
        imageService = ImageURLServiceImpl.getInstance();
        scriptureService = ScriptureServiceImpl.getInstance(activity);
        personManager = PersonManagerImpl.getInstance(activity);
        CeaselessApplication application = (CeaselessApplication) activity.getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        activity.setTitle(getString(R.string.nav_home));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        numberOfPeopleToPrayForDaily = Integer.parseInt(preferences.getString(Constants.NUMBER_OF_PEOPLE_TO_PRAY_FOR, "3"));
        return view;
    }

    private void prepareCache() {
        // if we're not using the cache, set the default page to the first
        if (!useCache) {
            Log.d(TAG, "resetting page index to 0");
            cacheManager.cachePageIndex(0);
        }

        // decide whether or not to fetch a new verse image
        String verseImageURL = cacheManager.getCachedVerseImageURL();
        if (!useCache || verseImageURL == null) {
            updateBackgroundImage();
            new ImageFetcher(activity).execute();
        }

        // verse title and text
        ScriptureData scriptureData = cacheManager.getCachedScripture();
        if (!useCache || scriptureData == null) {
            ScriptureData scripture = scriptureService.getScripture();
            if (scripture != null) {
                Log.d(TAG, "scripture = " + scripture);
                // cache
                cacheManager.cacheScripture(scripture);
            } else {
                Log.e(TAG, "Could not fetch scripture!");
            }
        }

        // people to pray for
        List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
        if (!useCache || personIds == null || personIds.size() < numberOfPeopleToPrayForDaily) {
            List<PersonPOJO> personPOJOs;
            try {
                personPOJOs = personManager.getNextPeopleToPrayFor(numberOfPeopleToPrayForDaily);
            } catch (AlreadyPrayedForAllContactsException e) {
                // TODO: Celebrate!
                try {
                    personPOJOs = personManager.getNextPeopleToPrayFor(numberOfPeopleToPrayForDaily);
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
        }

        // now that the cache is updated, mark it ready for use.
        useCache = true;
    }

    private void prepareViewPager() {
        viewPager.setOffscreenPageLimit(numberOfPeopleToPrayForDaily + 1);
        final FragmentStatePagerAdapter pagerAdapter = getFragmentStatePagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        // since this gets called multiple times, we need to clear any existing onpagechangelisteners.
        // otherwise the listeners will accumulate. For example, open a quickcontent intent and go back.
        // suddenly you have two onPageChangeListeners attached.
        // Question: Does this mean that we maybe don't even need to configure the viewpager every single time?
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ICardPageFragment card = (ICardPageFragment) pagerAdapter.getItem(position);
                cacheManager.cachePageIndex(viewPager.getCurrentItem());
                AnalyticsUtils.sendScreenViewHit(mTracker, card.getCardName());

                Bundle newState = new Bundle();
                newState.putInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG, position);
                if (position > 0 && position < numberOfPeopleToPrayForDaily + 1) {
                    List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
                    if (personIds != null && personIds.size() >= position) {
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

        // set the page if required
        Integer cachedPageIndex = cacheManager.getCachedPageIndex();
        if (cachedPageIndex != null) {
            Log.d(TAG, "setting pager to " + cachedPageIndex);
            viewPager.setCurrentItem(cachedPageIndex);
        }
    }

    private FragmentStatePagerAdapter getFragmentStatePagerAdapter() {
        return new FragmentStatePagerAdapter(activity.getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                android.support.v4.app.Fragment fragment;
                Bundle bundle = new Bundle();
                if (position == 0) {
                    fragment = new ScriptureCardSupportFragment();
                } else if (position == getCount() - 1) {
                    fragment = new ProgressCardSupportFragment();
                } else {
                    List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
                    // we need at least as many people as there are slots to fill
                    if (personIds != null && personIds.size() >= position) {
                        String personId = personIds.get(position - 1);
                        PersonPOJO person = personManager.getPerson(personId);
                        if (person == null) {
                            fragment = new BlankSupportFragment();
                        } else {
                            fragment = PersonSupportFragment.newInstance(personId);
                            bundle.putInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG, position);
                        }
                    } else {
                        fragment = new BlankSupportFragment();
                    }
                }

                // update the fragment's bundle so it has the data it needs
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
                if (cacheManager == null || cacheManager.getCachedPersonIdsToPrayFor() == null) {
                    return Constants.NUM_AUXILIARY_CARDS;
                }

                return cacheManager.getCachedPersonIdsToPrayFor().size()
                        + Constants.NUM_AUXILIARY_CARDS;
            }
        };
    }

    private void updateBackgroundImage() {
        File nextBackgroundImage = new File(activity.getCacheDir(), Constants.NEXT_BACKGROUND_IMAGE);
        File currentBackgroundImage = new File(activity.getCacheDir(), Constants.CURRENT_BACKGROUND_IMAGE);

        // move cached next background image
        if (nextBackgroundImage.exists()) {
            if (nextBackgroundImage.renameTo(currentBackgroundImage)) {
                Log.d(TAG, "Updated the background image to use from " + nextBackgroundImage + " to " + currentBackgroundImage);
                Log.d(TAG, "New image size: " + currentBackgroundImage.length());
                Picasso.with(activity).invalidate(currentBackgroundImage); // clear the picasso cache
                CommonUtils.setupBackgroundImage(activity, (ImageView) activity.findViewById(R.id.backgroundImageView));
            } else {
                Log.d(TAG, "Could not update the background image to use.");
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!activity.getHomeFragmentCreated()) {
            // if the activity was just created and hence the HomeFragment
            // was created for the first time, start on the first page.
            // otherwise, we start on the page where the HomeFragment was before
            cacheManager.cachePageIndex(0);
            activity.setHomeFragmentCreated(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.sendScreenViewHit(mTracker, "HomeScreen");
        boolean searchViewFocused = searchView != null && searchView.hasFocus();
        // create the pager so we see the cards
        if (activity.getHomeFragmentCreated() && !searchViewFocused) {
            prepareCache();
            prepareViewPager();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager =
                (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(activity.getComponentName()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class ImageFetcher extends AsyncTask<String, Void, String> {

        private Activity activity;

        public ImageFetcher(Activity activity) {
            this.activity = activity;
        }

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
                try {
                    // fetch new background image
                    File nextBackgroundImage = new File(activity.getCacheDir(), Constants.NEXT_BACKGROUND_IMAGE);
                    new DownloadFileAsyncTask(activity, imageUrl, nextBackgroundImage).execute();
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching scripture image!");
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Could not fetch scripture image!");
            }
        }
    }
}
