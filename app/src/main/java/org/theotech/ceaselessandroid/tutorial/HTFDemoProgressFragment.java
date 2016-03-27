package org.theotech.ceaselessandroid.tutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

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
    @Bind(R.id.watch_progress_arrow)
    IconTextView watchProgressArrow;
    @Bind(R.id.watch_progress_text)
    TextView watchProgressText;

    @Bind(R.id.exit_button)
    Button exitButton;

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

        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadMainFragment();
            }
        });

        return view;
    }

    private void animate() {
        final long fadeDuration = 1000;
        final long startFadeTime = 0;
        LinearInterpolator linInter = new LinearInterpolator();
        animateTooltip(fadeDuration, startFadeTime, linInter);
        animateButton();
    }

    private void animateTooltip(long fadeDuration, long startFadeTime, LinearInterpolator linInter) {
        AlphaAnimation mAlAnimation = new AlphaAnimation(0, 1);
        mAlAnimation.setDuration(fadeDuration);
        mAlAnimation.setStartOffset(startFadeTime);
        mAlAnimation.setInterpolator(linInter);

        watchProgressArrow.startAnimation(mAlAnimation);
        watchProgressText.startAnimation(mAlAnimation);
        watchProgressArrow.setVisibility(View.VISIBLE);
        watchProgressText.setVisibility(View.VISIBLE);
    }

    private void animateButton() {
        AlphaAnimation mAlAnimation = new AlphaAnimation(0, 1);
        mAlAnimation.setDuration(750);
        mAlAnimation.setStartOffset(750);
        mAlAnimation.setInterpolator(new LinearInterpolator());

        exitButton.startAnimation(mAlAnimation);
        exitButton.setVisibility(View.VISIBLE);
    }

    public void onSelected() {
        animate();
    }

}
