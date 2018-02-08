package org.theotech.ceaselessandroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.util.AnalyticsUtils;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.FragmentUtils;
import org.theotech.ceaselessandroid.util.Installation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressCardSupportFragment extends Fragment implements ICardPageFragment {
    private static final String TAG = ProgressCardSupportFragment.class.getSimpleName();
    @BindView(R.id.prayed_for_text)
    TextView prayedFor;
    @BindView(R.id.prayer_progress)
    ProgressBar progress;
    @BindView(R.id.show_more_people)
    LinearLayout showMorePeople;
    @BindView(R.id.progress_card_background)
    ImageView progressCardBackground;
    @BindView(R.id.number_of_days_praying)
    TextView numberOfDaysPraying;

    private PersonManager personManager = null;
    private CacheManager cacheManager = null;

    public ProgressCardSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        personManager = PersonManagerImpl.getInstance(getActivity());
        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view and bind
        View view = inflater.inflate(R.layout.fragment_support_progress_card, container, false);
        ButterKnife.bind(this, view);

        // progress
        long numPrayed = personManager.getNumPrayed();
        long numPeople = personManager.getNumPeople();
        prayedFor.setText(String.format(getString(R.string.prayed_for), numPrayed, numPeople));
        progress.setProgress((int) ((float) numPrayed / numPeople * 100.0f));
        progress.requestLayout();

        numberOfDaysPraying.setText(getString(R.string.day) + " " + cacheManager.numberOfCacheEntries());

        // TODO for performance reasons we do not make the progress card have a blurred background
        // until we can reuse a blurred version of the image to prevent out of memory issues.
        // CommonUtils.setDynamicImage(getActivity(), progressCardBackground);

        showMorePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnalyticsUtils.sendEventWithCategory(AnalyticsUtils.getDefaultTracker(getActivity()),
                        getString(R.string.ga_progress_card_actions),
                        getString(R.string.ga_tapped_show_more_people),
                        Installation.id(getActivity()));
                showMorePeople();
            }
        });

        return view;
    }

    private void showMorePeople() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.USE_CACHE_BUNDLE_ARG, false);
        FragmentUtils.loadFragment(getActivity(), getActivity().getFragmentManager(), null, R.id.show_more_people, bundle, null);
    }

    public String getCardName() {
        return "ProgressCard";
    }

}
