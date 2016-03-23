package org.theotech.ceaselessandroid.tutorial;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    @Bind(R.id.favorite_btn_arrow)
    IconTextView favoriteArrow;
    @Bind(R.id.message_btn_arrow)
    IconTextView messageArrow;
    @Bind(R.id.note_btn_arrow)
    IconTextView noteArrow;

    @Bind(R.id.favorite_btn_circle)
    IconTextView favoriteCircle;
    @Bind(R.id.message_btn_circle)
    IconTextView messageCircle;
    @Bind(R.id.note_btn_circle)
    IconTextView noteCircle;

    @Bind(R.id.favorite_btn_cover)
    IconTextView favoriteCover;
    @Bind(R.id.message_btn_cover)
    IconTextView messageCover;
    @Bind(R.id.note_btn_cover)
    IconTextView noteCover;

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
    @Bind(R.id.tool_tip_four)
    LinearLayout toolTipFour;

    private boolean showToolTip;
    private int sceneNum = 0;

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
        personImageOverlay.setVisibility(View.INVISIBLE);
        toolTipOne.setVisibility(View.VISIBLE);
        personImage.setOnClickListener(new HTFDemoPersonOnClickListener() {
            @Override
            public void onClick(View v) {
                personImageClick();
            }
        });

        toolTipOverlay.setOnClickListener(new HTFDemoPersonOnClickListener() {
            @Override
            public void onClick(View v) {
                overlayClick();
            }
        });

        toolTipOverlay.setClickable(false);

    }

    abstract class HTFDemoPersonOnClickListener implements View.OnClickListener {

        protected void personImageClick() {
            if (sceneNum < 2) {
                progressScene();
            }
        }

        protected void overlayClick() {
            if (sceneNum == 2 || sceneNum == 3) {
                progressScene();
            }
        }
        private void progressScene() {
            sceneNum++;
            Log.d(TAG, "Scene Num = " + sceneNum);
            switch (sceneNum) {
                case 1:
                    toolTipOne.setVisibility(View.INVISIBLE);
                    toolTipTwo.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    toolTipTwo.setVisibility(View.INVISIBLE);
                    personImageOverlay.setVisibility(View.VISIBLE);
                    favoriteCover.setVisibility(View.VISIBLE);
                    messageCover.setVisibility(View.VISIBLE);
                    noteCover.setVisibility(View.VISIBLE);
                    favoriteCircle.setVisibility(View.VISIBLE);
                    favoriteArrow.setVisibility(View.VISIBLE);
                    favoriteToolTip.setVisibility(View.VISIBLE);
                    toolTipThree.setVisibility(View.VISIBLE);
                    toolTipOverlay.setClickable(true);
                    break;
                case 3:
                    favoriteToolTip.setVisibility(View.INVISIBLE);
                    favoriteArrow.setVisibility(View.INVISIBLE);
                    favoriteCircle.setVisibility(View.INVISIBLE);
                    messageCircle.setVisibility(View.VISIBLE);
                    messageArrow.setVisibility(View.VISIBLE);
                    messageToolTip.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    messageToolTip.setVisibility(View.INVISIBLE);
                    messageArrow.setVisibility(View.INVISIBLE);
                    messageCircle.setVisibility(View.INVISIBLE);
                    toolTipThree.setVisibility(View.INVISIBLE);
                    noteCircle.setVisibility(View.VISIBLE);
                    noteArrow.setVisibility(View.VISIBLE);
                    noteToolTip.setVisibility(View.VISIBLE);
                    toolTipFour.setVisibility(View.VISIBLE);
                    toolTipOverlay.setClickable(false);
                    personImage.setClickable(false);
                    break;
            }

        }
    }

    private void setAllInvisible() {
        toolTipTwo.setVisibility(View.INVISIBLE);
        toolTipThree.setVisibility(View.INVISIBLE);
        toolTipFour.setVisibility(View.INVISIBLE);

        favoriteCover.setVisibility(View.INVISIBLE);
        favoriteCircle.setVisibility(View.INVISIBLE);
        favoriteArrow.setVisibility(View.INVISIBLE);
        favoriteToolTip.setVisibility(View.INVISIBLE);

        messageCover.setVisibility(View.INVISIBLE);
        messageCircle.setVisibility(View.INVISIBLE);
        messageArrow.setVisibility(View.INVISIBLE);
        messageToolTip.setVisibility(View.INVISIBLE);

        noteCover.setVisibility(View.INVISIBLE);
        noteCircle.setVisibility(View.INVISIBLE);
        noteArrow.setVisibility(View.INVISIBLE);
        noteToolTip.setVisibility(View.INVISIBLE);

    }

    public void onSelected() {
        if (showToolTip) {
            sceneNum = 0;
            setAllInvisible();
            setUpToolTip();
        }
    }

}

