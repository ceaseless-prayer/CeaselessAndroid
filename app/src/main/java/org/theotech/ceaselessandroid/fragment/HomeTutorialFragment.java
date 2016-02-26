package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
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

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.util.Constants;

public class HomeTutorialFragment extends Fragment {
    private static final String TAG = HomeTutorialFragment.class.getSimpleName();

    private boolean mCreated = false;
    private boolean useCache;
    private FragmentStateListener mListener;
    private TextView text;

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
        text = (TextView) view.findViewById(R.id.info_text);
        CardView card = (android.support.v7.widget.CardView) view.findViewById(R.id.card_view);
        card.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                GestureDetectorCompat mDetector = new GestureDetectorCompat(getActivity(), new HomeTutorialFragment.HTFGestureListener());
                mDetector.onTouchEvent(event);
                return true;
            }
        });

        return view;
    }

    class HTFGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            text.setText("onDown: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            text.setText("onFling: " + event1.toString() + event2.toString());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2,
                                float distanceX, float distanceY) {
            text.setText("onScroll: " + distanceX);
            return true;
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCreated = true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



}
