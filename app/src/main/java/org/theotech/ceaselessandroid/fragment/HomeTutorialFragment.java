package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.transformer.ZoomOutPageTransformer;
import org.theotech.ceaselessandroid.util.AnalyticsUtils;
import org.theotech.ceaselessandroid.util.Constants;

import java.util.List;

import butterknife.Bind;

public class HomeTutorialFragment extends Fragment {
    private static final String TAG = HomeTutorialFragment.class.getSimpleName();

    private final Handler handler = new Handler();

    @Bind(R.id.tutorial_viewpager)
    ViewPager viewPager;
    @Bind(R.id.tutorial_indicator)
    CirclePageIndicator indicator;

    private boolean mCreated = false;
    private boolean useCache;
    private Runnable runPager;
    private FragmentStateListener mListener;
    private TextView mText;
    private CardView[] mCard = new CardView[2];

    public HomeTutorialFragment() {
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
        mListener.notify(new FragmentState(getString(R.string.nav_home) + " (Tutorial)", currentState));

        // do not use the cache if we're trying to get more people.
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.USE_CACHE_BUNDLE_ARG)) {
            this.useCache = bundle.getBoolean(Constants.USE_CACHE_BUNDLE_ARG);
        } else {
            this.useCache = true;
        }


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {

        // set title
        String title = getString(R.string.nav_home) + " (Tutorial)";
        getActivity().setTitle(title);

        // create view
        View view = inflater.inflate(R.layout.fragment_home_tutorial, container, false);

        Button button = (Button) view.findViewById(R.id.intro_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadMainFragment(false);
            }
        });
        mText = (TextView) view.findViewById(R.id.info_text1);
//        mCard[0] = (CardView) view.findViewById(R.id.card_view1);
//        mCard[1] = (CardView) view.findViewById(R.id.card_view2);
//      mCard.setVisibility(View.GONE);
        TextView continueText = (TextView) view.findViewById(R.id.continue_text);
/*        continueText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                GestureDetectorCompat mDetector = new GestureDetectorCompat(getActivity(), new HomeTutorialFragment.HTFGestureListener());
                mDetector.onTouchEvent(event);
                return true;
            }
        });
*/
        runPager = new Runnable() {
            @Override
            public void run() {
                viewPager.setOffscreenPageLimit(0);
                final FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager()) {
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
                            // we need at least as many people as there are slots to fill
                            if (personIds != null && personIds.size() >= position) {
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
                        return numberOfPeopleToPrayForDaily + Constants.NUM_AUXILIARY_CARDS;
                    }
                };
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
                        Log.v(TAG, "Page selected " + position);
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
                Bundle bundle = getArguments();
                if (bundle != null && bundle.containsKey(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG)) {
                    Integer page = bundle.getInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG);
                    Log.d(TAG, "setting pager to " + page);
                    viewPager.setCurrentItem(page);
                } else {
                    // this defaults to page 0, scripture card
                    Log.d(TAG, "No bundle argument for page");
                    AnalyticsUtils.sendScreenViewHit(mTracker, ((ICardPageFragment) pagerAdapter.getItem(0)).getCardName());
                }
            }
        };


        return view;
    }
/*
    class HTFGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
//            mtext.setText("onDown: " + event.toString());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
//            mtext.setText("onLongPress: " + event.toString());
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
//            mCard[0].setVisibility(View.GONE);
//            mCard[1].setVisibility(View.VISIBLE);
//          mtext.setText("onScroll: " + distanceX);
            return true;
        }

    }
*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        if (mCreated && runPager != null) {
            handler.post(runPager);
        }
    }



}
