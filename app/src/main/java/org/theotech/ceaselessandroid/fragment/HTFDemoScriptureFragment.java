package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.theotech.ceaselessandroid.R;


public class HTFDemoScriptureFragment extends Fragment {

    private boolean mCreated = false;
    private TextView mText;


    public HTFDemoScriptureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // create view
        View view = inflater.inflate(R.layout.fragment_htfdemo_scripture, container, false);

        mText = (TextView) view.findViewById(R.id.info_text);
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
 //       LastUpdatedFragment fragB = new LastUpdatedFragment();
        Fragment verseFragment = new VerseCardSupportFragment();
        childFragTrans.add(R.id.frag_placeholder, verseFragment);
//        childFragTrans.addToBackStack("B");
        childFragTrans.commit();

        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCreated = true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



}
