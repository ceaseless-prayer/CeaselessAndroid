package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.theotech.ceaselessandroid.R;

import butterknife.ButterKnife;

/**
 * Created by chrislim on 10/31/15.
 */
public class BlankSupportFragment extends Fragment {

    public BlankSupportFragment() {
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
        View view = inflater.inflate(R.layout.fragment_support_blank_card, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

}
