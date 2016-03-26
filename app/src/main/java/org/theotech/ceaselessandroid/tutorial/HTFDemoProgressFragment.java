package org.theotech.ceaselessandroid.tutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * created by travis Feb/Mar 2016
 */

public class HTFDemoProgressFragment extends Fragment implements HTFDemoFragment {
    private static final String TAG = HTFDemoProgressFragment.class.getSimpleName();
    @Bind(R.id.prayed_for_text)
    TextView prayedFor;
    @Bind(R.id.prayer_progress)
    ProgressBar progress;
    @Bind(R.id.number_of_days_praying)
    TextView numberOfDaysPraying;

    @Bind(R.id.exit_button)
    Button mButton;

    public HTFDemoProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view and bind
        View view = inflater.inflate(R.layout.fragment_htfdemo_progress, container, false);
        ButterKnife.bind(this, view);

        // progress
        long numPrayed = 3;
        long numPeople = 130;
        prayedFor.setText(String.format(getString(R.string.prayed_for), numPrayed, numPeople));
        progress.setProgress((int) ((float) numPrayed / numPeople * 100.0f));
        progress.requestLayout();

        numberOfDaysPraying.setText(getString(R.string.day) + " " + 1);

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadMainFragment();
            }
        });

        return view;
    }

    public void onSelected() {}

}
