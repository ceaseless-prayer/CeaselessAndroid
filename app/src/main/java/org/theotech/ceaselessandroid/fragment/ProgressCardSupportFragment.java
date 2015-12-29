package org.theotech.ceaselessandroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.FragmentUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProgressCardSupportFragment extends Fragment {

    @Bind(R.id.prayed_for_text)
    TextView prayedFor;
    @Bind(R.id.prayer_progress)
    ProgressBar progress;
    @Bind(R.id.show_more_people)
    Button showMorePeople;

    private PersonManager personManager = null;

    public ProgressCardSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        personManager = PersonManagerImpl.getInstance(getActivity());
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

        showMorePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

}
