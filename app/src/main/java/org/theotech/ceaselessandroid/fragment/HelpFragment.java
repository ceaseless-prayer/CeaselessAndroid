package org.theotech.ceaselessandroid.fragment;


import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.util.AnalyticsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpFragment extends Fragment {

    @BindView(R.id.helpWebView)
    WebView helpWV;

    private FragmentStateListener mListener;

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
        helpWV.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                if (url.startsWith("mailto:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
        helpWV.loadUrl(getString(R.string.help_url));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.sendScreenViewHit(this.getActivity(), "HelpScreen");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.help_menu, menu);
        MenuItem startTutorial = menu.findItem(R.id.start_tutorial);

        startTutorial.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ((MainActivity) getActivity()).loadHomeTutorialFragment();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
