package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.util.FragmentUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chrislim on 10/31/15.
 * A blank placeholder card.
 */
public class BlankSupportFragment extends Fragment implements ICardPageFragment {

    @BindView(R.id.contact_us_button)
    Button contactUsButton;

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
        contactUsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FragmentUtils.loadFragment(getActivity(), getActivity().getFragmentManager(), null,
                        R.id.nav_contact_us, new FragmentState(getString(R.string.nav_home)));
            }
        });
        return view;
    }

    public String getCardName() {
        return "BlankCard";
    }
}
