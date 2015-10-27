package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonFragment extends Fragment {

    @Bind(R.id.person_name)
    TextView personName;
    @Bind(R.id.person_image)
    ImageView personImage;
    @Bind(R.id.person_notes_list)
    LinearLayout notes;

    private PersonManager personManager;

    public PersonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        personManager = PersonManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_people));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_support_person, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.PERSON_ID_BUNDLE_ARG)) {
            String personId = bundle.getString(Constants.PERSON_ID_BUNDLE_ARG);
            CommonUtils.displayPerson(getActivity(), personManager, personName, personImage,
                    notes, view, personId, getString(R.string.empty_notes));
        }
        return view;
    }

}
