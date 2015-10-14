package org.theotech.ceaselessandroid.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.LinePageIndicator;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PeopleFragment extends Fragment {
    private static final String TAG = PeopleFragment.class.getSimpleName();

    private final Handler handler = new Handler();
    @Bind(R.id.person_viewer)
    ViewPager viewPager;
    private Runnable runPager;
    private boolean mCreated = false;

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getString(R.string.nav_people));
        final View view = inflater.inflate(R.layout.fragment_people, container, false);
        ButterKnife.bind(this, view);

        runPager = new Runnable() {
            @Override
            public void run() {
                viewPager.setOffscreenPageLimit(Constants.NUM_PERSONS - 1);
                viewPager.setAdapter(new FragmentStatePagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager()) {
                    @Override
                    public android.support.v4.app.Fragment getItem(int position) {
                        return PersonFragment.newInstance(position);
                    }

                    @Override
                    public int getCount() {
                        return Constants.NUM_PERSONS;
                    }
                });
                // wire up the indicator
                LinePageIndicator indicator = (LinePageIndicator) view.findViewById(R.id.indicator);
                indicator.setViewPager(viewPager);
                Bundle bundle = getArguments();
                if (bundle != null && bundle.containsKey(Constants.PERSON_ARG_SECTION_NUMBER)) {
                    int itemIndex = bundle.getInt(Constants.PERSON_ARG_SECTION_NUMBER);
                    viewPager.setCurrentItem(itemIndex);
                    indicator.setCurrentItem(itemIndex);
                }
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
}
