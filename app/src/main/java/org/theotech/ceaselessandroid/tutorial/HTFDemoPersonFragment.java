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

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * created by travis Feb/Mar 2016
 */

public class HTFDemoPersonFragment extends Fragment implements HTFDemoFragment {
    private static final String TAG = HTFDemoPersonFragment.class.getSimpleName();

    @Bind(R.id.person_name)
    TextView personName;
    @Bind(R.id.person_image)
    RoundedImageView personImage;
    @Bind(R.id.tool_tip_overlay)
    LinearLayout toolTipOverlay;
    @Bind(R.id.person_image_overlay)
    View personImageOverlay;
    @Bind(R.id.favorite_btn_tooltip)
    TextView favoriteToolTip;
    @Bind(R.id.message_btn_tooltip)
    TextView messageToolTip;
    @Bind(R.id.note_btn_tooltip)
    TextView noteToolTip;
    @Bind(R.id.tool_tip_one)
    LinearLayout toolTipOne;
    @Bind(R.id.tool_tip_two)
    LinearLayout toolTipTwo;
    @Bind(R.id.tool_tip_three)
    LinearLayout toolTipThree;


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

        injectPersonIntoView(getActivity(), personName, view);

        if (showToolTip) {
            setUpToolTip();
        }
        return view;
    }

    private void injectPersonIntoView(Activity activity, String name, View view) {

        // display name and picture

        personName.setText(name);
        Picasso.with(activity).load(R.drawable.placeholder_user)
                .fit().centerInside().into(personImage);
        // display (empty) notes
        ListView emptyNotes = (ListView) view.findViewById(R.id.empty_person_notes);
        emptyNotes.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item_empty_notes, new String[]{getString(R.string.empty_notes)}));

    }

    private void setUpToolTip() {
        toolTipOverlay.setVisibility(View.VISIBLE);
        noteToolTip.setVisibility(View.VISIBLE);
        toolTipThree.setVisibility(View.VISIBLE);
//        personImageOverlay.setVisibility(View.INVISIBLE);
//        toolTips.setVisibility(View.VISIBLE);
        toolTipOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolTipOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void onSelected() {
        if (showToolTip) {
            toolTipOverlay.setVisibility(View.VISIBLE);
//            toolTips.setVisibility(View.VISIBLE);
        }
    }

}

