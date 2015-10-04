package org.theotech.ceaselessandroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PersonFragment() {
        // Required empty public constructor
    }

    public static PersonFragment newInstance(int sectionNumber) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        TextView personViewText = (TextView) view.findViewById(R.id.person_view_text);
        int id = getArguments().getInt(ARG_SECTION_NUMBER) + 1;
        personViewText.setText("Person view " + id);

        return view;
    }


}
