package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.gms.analytics.Tracker;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.util.Constants;

public class HomeTutorialFragment extends Fragment {
//    private static final String TAG = HomeTutorialFragment.class.getSimpleName();

    private final Handler handler = new Handler();
/*
    @Bind(R.id.home_view)
    ViewPager viewPager;
    @Bind(R.id.home_indicator)
    CirclePageIndicator indicator;
*/
    private Runnable runPager;
    private boolean mCreated = false;
    private boolean useCache;
//    private boolean showTutorial;
    private FragmentStateListener mListener;

//    private Tracker mTracker;

    private SearchView searchView;
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
        currentState.putInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG, 0);
        mListener.notify(new FragmentState(getString(R.string.nav_home)+" (Tutorial)", currentState));

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

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_home_tutorial, container, false);
        // ButterKnife.bind(this, view);

        Button button = (Button) view.findViewById(R.id.intro_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadMainFragment(false);
//                getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment(), getString(R.string.nav_home) ).commit();
            }
        });
        return view;
    }



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
   //     AnalyticsUtils.sendScreenViewHit(mTracker, "HomeScreen");
        //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean searchViewFocused = searchView != null && searchView.hasFocus();

        // activate the pager so we see the cards
        if (mCreated && runPager != null && !searchViewFocused) {
            handler.post(runPager);
        }
    }



}
