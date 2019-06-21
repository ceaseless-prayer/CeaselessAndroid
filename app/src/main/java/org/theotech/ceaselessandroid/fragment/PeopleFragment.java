package org.theotech.ceaselessandroid.fragment;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.viewpagerindicator.UnderlinePageIndicator;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.util.AnalyticsUtils;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.Container;
import org.theotech.ceaselessandroid.util.FragmentUtils;
import org.theotech.ceaselessandroid.util.Refreshable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeopleFragment extends Fragment {
    private static final String TAG = PeopleFragment.class.getSimpleName();
    private final Handler handler = new Handler();
    @BindView(R.id.people_view)
    ViewPager viewPager;
    @BindView(R.id.people_indicator)
    UnderlinePageIndicator indicator;
    @BindView(R.id.people_active_tab)
    TextView activeTab;
    @BindView(R.id.people_favorite_tab)
    TextView favoriteTab;
    @BindView(R.id.people_removed_tab)
    TextView removedTab;
    private String[] TABS;
    private Runnable runPager;
    private FragmentStateListener mListener;
    private boolean mCreated = false;
    private boolean showingPerson = false;

    public PeopleFragment() {
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
        mListener.notify(new FragmentState(getString(R.string.nav_people)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_people));

        // set tab titles
        TABS = new String[]{getString(R.string.people_active), getString(R.string.people_favorite), getString(R.string.people_removed)};

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        ButterKnife.bind(this, view);

        // wire up the people view pager
        runPager = new Runnable() {
            @Override
            public void run() {
                final Container<Refreshable> activeRefreshable = new Container<>();
                final Container<Refreshable> favoriteRefreshable = new Container<>();
                final Container<Refreshable> removedRefreshable = new Container<>();

                viewPager.setAdapter(new FragmentStatePagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager()) {
                    @Override
                    public androidx.fragment.app.Fragment getItem(int position) {
                        if (position == 0) {
                            PeopleActiveSupportFragment activePeopleFragment = new PeopleActiveSupportFragment();
                            activeRefreshable.set(activePeopleFragment);
                            return activePeopleFragment;
                        } else if (position == 1) {
                            PeopleFavoriteSupportFragment favoritePeopleFragment = new PeopleFavoriteSupportFragment();
                            favoriteRefreshable.set(favoritePeopleFragment);
                            return favoritePeopleFragment;
                        } else if (position == 2) {
                            PeopleRemovedSupportFragment removedPeopleFragment = new PeopleRemovedSupportFragment();
                            removedRefreshable.set(removedPeopleFragment);
                            return removedPeopleFragment;
                        }
                        return null;
                    }

                    @Override
                    public int getCount() {
                        return TABS.length;
                    }
                });
                viewPager.clearOnPageChangeListeners();
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        final int activeTextColor = getResources().getColor(R.color.peopleTabActiveTextColor);
                        final int activeBackground = getResources().getColor(R.color.peopleTabActiveBackground);
                        final int inactiveTextColor = getResources().getColor(R.color.peopleTabInactiveTextColor);
                        final int inactiveBackground = getResources().getColor(R.color.peopleTabInactiveBackground);

                        if (position == 0) {
                            activeTab.setTextColor(activeTextColor);
                            activeTab.setBackgroundColor(activeBackground);
                            favoriteTab.setTextColor(inactiveTextColor);
                            favoriteTab.setBackgroundColor(inactiveBackground);
                            removedTab.setTextColor(inactiveTextColor);
                            removedTab.setBackgroundColor(inactiveBackground);
                            activeRefreshable.get().refreshList();
                        } else if (position == 1) {
                            activeTab.setTextColor(inactiveTextColor);
                            activeTab.setBackgroundColor(inactiveBackground);
                            favoriteTab.setTextColor(activeTextColor);
                            favoriteTab.setBackgroundColor(activeBackground);
                            removedTab.setTextColor(inactiveTextColor);
                            removedTab.setBackgroundColor(inactiveBackground);
                            favoriteRefreshable.get().refreshList();
                        } else if (position == 2) {
                            activeTab.setTextColor(inactiveTextColor);
                            activeTab.setBackgroundColor(inactiveBackground);
                            favoriteTab.setTextColor(inactiveTextColor);
                            favoriteTab.setBackgroundColor(inactiveBackground);
                            removedTab.setTextColor(activeTextColor);
                            removedTab.setBackgroundColor(activeBackground);
                            removedRefreshable.get().refreshList();
                        }
                        activeRefreshable.get().dismissActionMode();
                        favoriteRefreshable.get().dismissActionMode();
                        removedRefreshable.get().dismissActionMode();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
                // wire up the indicator
                indicator.setViewPager(viewPager);
            }
        };

        // wire up the active, favorite, and removed tabs
        activeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        favoriteTab.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               viewPager.setCurrentItem(1);
           }
        });
        removedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.PERSON_ID_BUNDLE_ARG)) {
            FragmentUtils.loadFragment(getActivity(), getActivity().getFragmentManager(), null,
                    R.id.person_card, bundle, new FragmentState(getString(R.string.nav_people)));
            showingPerson = true;
        } else {
            showingPerson = false;
        }
        mCreated = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.people_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (runPager != null && mCreated && !showingPerson) {
            handler.post(runPager);
        }
        AnalyticsUtils.sendScreenViewHit(this.getActivity(), "PeopleScreen");
    }
}
