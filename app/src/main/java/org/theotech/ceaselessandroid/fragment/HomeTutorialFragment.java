package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeTutorialFragment extends Fragment {
    private static final String TAG = HomeTutorialFragment.class.getSimpleName();

//    private final Handler handler = new Handler();

    @Bind(R.id.tutorial_viewpager)
    ViewPager viewPager;
    @Bind(R.id.tutorial_indicator)
    CirclePageIndicator indicator;

    private boolean mCreated = false;
    private boolean useCache;
//    private Runnable runPager;
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
        ButterKnife.bind(this, view);
/*
        Button button = (Button) view.findViewById(R.id.intro_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadMainFragment(false);
            }
        });
        mText = (TextView) view.findViewById(R.id.info_text1);

        */
//        mCard[0] = (CardView) view.findViewById(R.id.card_view1);
//        mCard[1] = (CardView) view.findViewById(R.id.card_view2);
//      mCard.setVisibility(View.GONE);
//        TextView continueText = (TextView) view.findViewById(R.id.continue_text);
/*        continueText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                GestureDetectorCompat mDetector = new GestureDetectorCompat(getActivity(), new HomeTutorialFragment.HTFGestureListener());
                mDetector.onTouchEvent(event);
                return true;
            }
        });
*/
        HomeTutorialFragment.HTFFragmentPagerAdapter pagerAdapter =
                new HomeTutorialFragment.HTFFragmentPagerAdapter(
                        ((AppCompatActivity) getActivity()).getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        return view;
    }

    class HTFFragmentPagerAdapter extends FragmentPagerAdapter {

        public HTFFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment fragment;
            fragment = new HTFHomeViewFragment();
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
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
//        handler.removeCallbacks(runPager);
    }

    @Override
    public void onResume() {
        super.onResume();
/*
        if (mCreated && runPager != null) {
            handler.post(runPager);
        }
 */
    }



}
