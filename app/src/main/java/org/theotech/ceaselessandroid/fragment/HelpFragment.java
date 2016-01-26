package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;

import com.google.android.gms.analytics.Tracker;

import org.theotech.ceaselessandroid.CeaselessApplication;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.util.AnalyticsUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HelpFragment extends Fragment {

    @Bind(R.id.helpWebView)
    WebView helpWV;

    private FragmentStateListener mListener;
    private Tracker mTracker;

    public HelpFragment() {
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
        mListener.notify(new FragmentState(getString(R.string.nav_help)));
        CeaselessApplication application = (CeaselessApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_help));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, view);

        helpWV.getSettings().setJavaScriptEnabled(true);
        helpWV.loadUrl(getString(R.string.help_url));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.sendScreenViewHit(mTracker, "HelpScreen");
    }
}
