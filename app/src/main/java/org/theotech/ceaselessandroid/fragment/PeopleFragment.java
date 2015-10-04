package org.theotech.ceaselessandroid.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PeopleFragment extends Fragment {
    @Bind(R.id.person_viewer)
    ViewPager viewPager;

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getString(R.string.nav_people));
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        ButterKnife.bind(this, view);

        viewPager.setAdapter(new  FragmentStatePagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return PersonFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return Constants.NUM_PERSONS;
            }
        });

        return view;
    }
}
