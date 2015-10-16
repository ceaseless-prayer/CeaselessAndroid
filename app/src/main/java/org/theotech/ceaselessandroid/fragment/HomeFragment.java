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

import com.viewpagerindicator.LinePageIndicator;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private final Handler handler = new Handler();

    @Bind(R.id.home_view)
    ViewPager viewPager;
    @Bind(R.id.indicator)
    LinePageIndicator indicator;

    private Runnable runPager;
    private boolean mCreated = false;
    private boolean useCache;
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

        /*
         * cache data if needed
         */
        // verse image
        String verseImageURL = cacheManager.getCachedVerseImageURL();
        if (!useCache || verseImageURL == null) {
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
            List<PersonPOJO> personPOJOs = null;
            try {
                personPOJOs = personManager.getNextPeopleToPrayFor(Constants.NUM_PERSONS);
            } catch (AlreadyPrayedForAllContactsException e) {
                // TODO: Celebrate!
                try {
                    personPOJOs = personManager.getNextPeopleToPrayFor(Constants.NUM_PERSONS);
                } catch (AlreadyPrayedForAllContactsException e1) {
                    // TODO: Something is really wrong if this happens, not sure what to do here
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
                            fragment = new VerseCardFragment();
                        } else if (position == getCount() - 1) {
                            fragment = new ProgressCardFragment();
                        } else {
                            fragment = PersonFragment.newInstance(position - 1);
                            bundle.putInt(Constants.PERSON_SECTION_NUMBER_BUNDLE_ARG, position - 1);
                        }
                        bundle.putBoolean(Constants.USE_CACHE_BUNDLE_ARG, useCache);
                        fragment.setArguments(bundle);

                        return fragment;
                    }

                    @Override
                    public int getCount() {
                        return Constants.NUM_PERSONS + 2;
                    }
                });
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                // wire up the indicator
                indicator.setViewPager(viewPager);
            }
        };
        if (mCreated) {
            handler.post(runPager);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (runPager != null) handler.post(runPager);
        mCreated = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(runPager);
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
                handler.post(runPager);
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
