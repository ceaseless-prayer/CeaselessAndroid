package org.theotech.ceaselessandroid.tutorial;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HTFDemoPersonFragment extends Fragment {
    private static final String TAG = HTFDemoPersonFragment.class.getSimpleName();

    @Bind(R.id.person_name)
    TextView personName;
    @Bind(R.id.person_image)
    RoundedImageView personImage;
    @Bind(R.id.person_notes_list)
    ListView notes;
    @Bind(R.id.note_btn)
    IconTextView noteButton;
    @Bind(R.id.tool_tip_layout)
    LinearLayout toolTipLayout;
//    private CacheManager cacheManager;
//    private PersonManager personManager;

    private boolean showToolTip;

    public HTFDemoPersonFragment() {
        // Required empty public constructor
    }

    public static HTFDemoPersonFragment newInstance(String personName, boolean showToolTip) {
        HTFDemoPersonFragment fragment = new HTFDemoPersonFragment();
        Bundle args = new Bundle();
        args.putString(Constants.DEMO_NAME_BUNDLE_ARG, personName);
        args.putBoolean(Constants.DEMO_TOOLTIP_BUNDLE_ARG, showToolTip);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

 //       cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
 //       personManager = PersonManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view and bind
        View view = inflater.inflate(R.layout.fragment_htfdemo_person, container, false);
        ButterKnife.bind(this, view);

        // display person
        Bundle bundle = getArguments();
        String personName = bundle.getString(Constants.DEMO_NAME_BUNDLE_ARG);
        showToolTip = bundle.getBoolean(Constants.DEMO_TOOLTIP_BUNDLE_ARG);
  /*      if (bundle != null && bundle.containsKey(Constants.PERSON_ID_BUNDLE_ARG) && bundle.containsKey(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG)) {
            final String personId = bundle.getString(Constants.PERSON_ID_BUNDLE_ARG);
            int homeIndex = bundle.getInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG);
            final FragmentState backStackInfo = new FragmentState(getString(R.string.nav_home));
            Bundle currentState = new Bundle();
            currentState.putInt(Constants.HOME_SECTION_NUMBER_BUNDLE_ARG, homeIndex);
            backStackInfo.setState(currentState);

            CommonUtils.injectPersonIntoView(getActivity(), personManager, personName, personImage,
                    notes, view, personId, getString(R.string.empty_notes), getActivity().getFragmentManager(),
                    backStackInfo);

            CommonUtils.wireAddNote(noteButton, personId, getActivity(), backStackInfo);
            CommonUtils.wireFavoriteShortcut(getActivity(), view, personId, personManager);
            CommonUtils.wireSendMessage(getActivity(), view, personId);
            CommonUtils.wireShowPersonMenu(view, personId, getActivity(), backStackInfo, personManager);
        }
*/
        injectPersonIntoView(getActivity(), personName, view);


        return view;
    }

    private void injectPersonIntoView(Activity activity, String name, View view) {
 //       PersonPOJO personPOJO = personManager.getPerson(personId);
 //       Uri personPhotoUri = CommonUtils.getContactPhotoUri(activity.getContentResolver(), personPOJO.getId(), true);

        // display name and picture

        personName.setText(name);
        Picasso.with(activity).load(R.drawable.placeholder_user)
                .fit().centerInside().into(personImage);

        TextView removedLabel = (TextView) view.findViewById(R.id.person_removed_label);
        removedLabel.setVisibility(View.INVISIBLE);

        // display (empty) notes

        ListView emptyNotes = (ListView) view.findViewById(R.id.empty_person_notes);
        emptyNotes.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item_empty_notes, new String[]{getString(R.string.empty_notes)}));

    }

    public void onResume() {
        super.onResume();
        if (showToolTip) {
            toolTipLayout.setVisibility(View.VISIBLE);
            toolTipLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toolTipLayout.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
/*
    public String getCardName() {
        return "PersonSupportCard";
    }
*/
}

