package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonFragment extends Fragment {

    private static final String TAG = PersonFragment.class.getSimpleName();
    @Bind(R.id.person_name)
    TextView personName;
    @Bind(R.id.person_image)
    RoundedImageView personImage;
    @Bind(R.id.person_notes_list)
    ListView notes;
    @Bind(R.id.note_btn)
    IconTextView noteButton;

    private FragmentStateListener mListener;
    private PersonManager personManager;

    public PersonFragment() {
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
        mListener.notify(new FragmentState(getString(R.string.person_view), getArguments()));

        personManager = PersonManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_people));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        ButterKnife.bind(this, view);

        // display person
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.PERSON_ID_BUNDLE_ARG)) {
            final String personId = bundle.getString(Constants.PERSON_ID_BUNDLE_ARG);
            final FragmentState backStackInfo = new FragmentState(getString(R.string.person_view));
            Bundle currentState = new Bundle();
            currentState.putString(Constants.PERSON_ID_BUNDLE_ARG, personId);
            backStackInfo.setState(currentState);

            CommonUtils.injectPersonIntoView(getActivity(), personManager, personName, personImage,
                    notes, view, personId, getString(R.string.empty_notes), getActivity().getFragmentManager(),
                    backStackInfo);

            CommonUtils.wireAddNote(noteButton, personId, getActivity(), backStackInfo);
            CommonUtils.wireFavoriteShortcut(view, personId, personManager, getString(R.string.favorite_on), getString(R.string.favorite_off));
            CommonUtils.wireSendMessage(getActivity(), view, personId);
            CommonUtils.wireShowPersonMenu(personImage, getActivity());
        }

        return view;
    }
}
