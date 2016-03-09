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
import org.theotech.ceaselessandroid.util.Constants;


public class HTFDemoPersonFragment extends Fragment {

    private boolean mCreated = false;
    private TextView mText;


    public HTFDemoPersonFragment() {
        // Required empty public constructor
    }

    public static HTFDemoPersonFragment newInstance(String personId) {
        HTFDemoPersonFragment fragment = new HTFDemoPersonFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PERSON_ID_BUNDLE_ARG, personId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String examplePerson = "";
        // create view
        View view = inflater.inflate(R.layout.fragment_htfdemo_person, container, false);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.PERSON_ID_BUNDLE_ARG)) {
            examplePerson = bundle.getString(Constants.PERSON_ID_BUNDLE_ARG);
        }
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        Fragment personFragment = PersonSupportFragment.newInstance(examplePerson);
        childFragTrans.add(R.id.frag_placeholder, personFragment);
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
