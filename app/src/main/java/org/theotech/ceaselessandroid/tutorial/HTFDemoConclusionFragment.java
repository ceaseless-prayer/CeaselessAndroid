package org.theotech.ceaselessandroid.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;


public class HTFDemoConclusionFragment extends Fragment {

    private boolean mCreated = false;
    private Button mButton;

    public HTFDemoConclusionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_htfdemo_conclusion, container, false);

        mButton = (Button) view.findViewById(R.id.exit_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadMainFragment();
            }
        });


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
