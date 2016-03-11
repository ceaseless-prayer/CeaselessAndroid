package org.theotech.ceaselessandroid.tutorial;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.fragment.FragmentStateListener;
import org.theotech.ceaselessandroid.transformer.ZoomOutPageTransformer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * created by travis Feb.2016
 */

public class HomeTutorialFragment extends Fragment {
    private static final String TAG = HomeTutorialFragment.class.getSimpleName();

    @Bind(R.id.tutorial_viewpager)
    ViewPager viewPager;
    @Bind(R.id.tutorial_indicator)
    CirclePageIndicator indicator;

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
        mListener.notify(new FragmentState(getString(R.string.nav_home_tutorial), currentState));

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {

        // set title
        String title = getString(R.string.nav_home_tutorial);
        getActivity().setTitle(title);

        // create view
        View view = inflater.inflate(R.layout.fragment_home_tutorial, container, false);
        ButterKnife.bind(this, view);

        HomeTutorialFragment.HTFFragmentStatePagerAdapter pagerAdapter =
                new HomeTutorialFragment.HTFFragmentStatePagerAdapter(
                        ((AppCompatActivity) getActivity()).getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                ((HTFDemoFragment) ((HTFFragmentStatePagerAdapter) viewPager.getAdapter()).getItem(i)).onSelected();
            }
        });
        indicator.setViewPager(viewPager);
        return view;
    }

    class HTFFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        private android.support.v4.app.Fragment[] fragArray;
        public HTFFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
            fragArray = new android.support.v4.app.Fragment[6];
            fragArray[0] = new HTFDemoScriptureFragment();
            fragArray[1] = HTFDemoPersonFragment.newInstance("Bob Flores", true);
            fragArray[2] = HTFDemoPersonFragment.newInstance("Joy Wong", false);
            fragArray[3] = HTFDemoPersonFragment.newInstance("Abby Anderson", false);
            fragArray[4] = new HTFDemoProgressFragment();
            fragArray[5] = new HTFDemoConclusionFragment();
        }


        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return fragArray[i];
        }

        @Override
        public int getCount() {
            return 6;
        }

    }


}
