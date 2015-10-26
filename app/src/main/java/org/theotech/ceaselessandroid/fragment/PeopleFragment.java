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
import android.widget.TextView;

import com.viewpagerindicator.UnderlinePageIndicator;

import org.theotech.ceaselessandroid.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PeopleFragment extends Fragment {
    private static final String TAG = PeopleFragment.class.getSimpleName();
    private final Handler handler = new Handler();
    @Bind(R.id.people_view)
    ViewPager viewPager;
    @Bind(R.id.people_indicator)
    UnderlinePageIndicator indicator;
    @Bind(R.id.people_active)
    TextView activeTab;
    @Bind(R.id.people_removed)
    TextView removedTab;
    private String[] TABS;
    private Runnable runPager;
    private FragmentStateListener mListener;
    private boolean mCreated = false;

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // fragment listener
        try {
            mListener = (FragmentStateListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FragmentStateListener");
        }
        mListener.notify(new FragmentState(getString(R.string.nav_people)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_people));

        // set tab titles
        TABS = new String[]{getString(R.string.people_active), getString(R.string.people_removed)};

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        ButterKnife.bind(this, view);

        // get default TextView text color
        final int defaultColor = activeTab.getCurrentTextColor();

        // wire up the people view pager
        runPager = new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(new FragmentStatePagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager()) {
                    @Override
                    public android.support.v4.app.Fragment getItem(int position) {
                        if (position == 0) {
                            return new PeopleActiveFragment();
                        } else if (position == 1) {
                            return new PeopleRemovedFragment();
                        }
                        return null;
                    }

                    @Override
                    public int getCount() {
                        return TABS.length;
                    }
                });
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 0) {
                            activeTab.setTextColor(getResources().getColor(R.color.cardLabel));
                            removedTab.setTextColor(defaultColor);
                        } else if (position == 1) {
                            activeTab.setTextColor(defaultColor);
                            removedTab.setTextColor(getResources().getColor(R.color.cardLabel));
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
                // wire up the indicator
                indicator.setViewPager(viewPager);
            }
        };

        // wire up the active and removed tabs
        activeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        activeTab.setTextColor(getResources().getColor(R.color.cardLabel));
        removedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });

        if (mCreated) {
            handler.post(runPager);
        }

        return view;
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
}
