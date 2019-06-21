package org.theotech.ceaselessandroid.tutorial;


import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * created by travis Feb/Mar 2016
 */

public class HTFDemoPersonFragment extends Fragment implements HTFDemoFragment {
    private static final String TAG = HTFDemoPersonFragment.class.getSimpleName();

    @BindView(R.id.person_name)
    TextView personName;
    @BindView(R.id.person_image)
    RoundedImageView personImage;

    @BindView(R.id.tool_tip_overlay)
    LinearLayout toolTipOverlay;
    @BindView(R.id.person_image_overlay)
    View personImageOverlay;

    @BindView(R.id.favorite_tooltip)
    RelativeLayout favoriteTooltip;
    @BindView(R.id.message_tooltip)
    RelativeLayout messageTooltip;
    @BindView(R.id.add_note_tooltip)
    RelativeLayout addNoteTooltip;

    @BindView(R.id.tool_tip_one)
    RelativeLayout toolTipOne;
    @BindView(R.id.tool_tip_two)
    TextView toolTipTwo;
    @BindView(R.id.tool_tip_four)
    LinearLayout toolTipFour;

    @BindView(R.id.up_arrow)
    IconTextView upArrow;
    @BindView(R.id.right_arrow)
    IconTextView rightArrow;

    private boolean showToolTip;
    private int sceneNum = 0;
    private PopupMenu popup;
    private View view;
    private boolean menuItemClicked = false;

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
        view = inflater.inflate(R.layout.fragment_htfdemo_person, container, false);
        ButterKnife.bind(this, view);

        // display person
        Bundle bundle = getArguments();
        String personName = bundle.getString(Constants.DEMO_NAME_BUNDLE_ARG);
        showToolTip = bundle.getBoolean(Constants.DEMO_TOOLTIP_BUNDLE_ARG);

        injectPersonIntoView(getActivity(), personName, view);

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
        popup = getMenu();

        personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressScene();
            }
        });

        toolTipOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressScene();
            }
        });

        toolTipOverlay.setClickable(false);

    }

    private void animate() {
        final long fadeDuration = 750;
        final long startFadeTime = 400;

        LinearInterpolator linInter = new LinearInterpolator();
        animateRightArrow(linInter);
        animateUpArrow(linInter);
        animateTooltip(fadeDuration, startFadeTime, linInter);

    }

    private void animateTooltip(long fadeDuration, long startFadeTime, LinearInterpolator linInter) {
        AlphaAnimation mAlAnimation = new AlphaAnimation(0, 1);
        mAlAnimation.setDuration(fadeDuration);
        mAlAnimation.setStartOffset(startFadeTime);
        mAlAnimation.setInterpolator(linInter);
        toolTipOverlay.setAnimation(mAlAnimation);
        toolTipOne.setAnimation(mAlAnimation);
    }

    private void animateUpArrow(LinearInterpolator linInter) {
        TranslateAnimation mVerAnimation = new TranslateAnimation(
                0, 0, -3, 3);
        mVerAnimation.setDuration(250);
        mVerAnimation.setStartOffset(20);
        mVerAnimation.setRepeatCount(-1);
        mVerAnimation.setRepeatMode(Animation.REVERSE);
        mVerAnimation.setInterpolator(linInter);
        upArrow.setAnimation(mVerAnimation);
    }

    private void animateRightArrow(LinearInterpolator linInter) {
        TranslateAnimation mHorAnimation = new TranslateAnimation(
                -4, 4, 0, 0);
        mHorAnimation.setDuration(250);
        mHorAnimation.setStartOffset(20);
        mHorAnimation.setRepeatCount(-1);
        mHorAnimation.setRepeatMode(Animation.REVERSE);
        mHorAnimation.setInterpolator(linInter);
        rightArrow.setAnimation(mHorAnimation);
    }

    private void progressScene() {
        sceneNum++;
        Log.d(TAG, "Scene Num = " + sceneNum);
        switch (sceneNum) {
            case 1:
                toolTipOne.setVisibility(View.INVISIBLE);
                toolTipTwo.setVisibility(View.VISIBLE);
                personImage.setClickable(false);
                popup.show();
                break;
            case 2:
                personImageOverlay.setVisibility(View.VISIBLE);
                favoriteTooltip.setVisibility(View.VISIBLE);
                toolTipOverlay.setClickable(true);
                break;
            case 3:
                favoriteTooltip.setVisibility(View.INVISIBLE);
                messageTooltip.setVisibility(View.VISIBLE);
                break;
            case 4:
                toolTipTwo.setVisibility(View.INVISIBLE);
                messageTooltip.setVisibility(View.INVISIBLE);
                addNoteTooltip.setVisibility(View.VISIBLE);
                toolTipFour.setVisibility(View.VISIBLE);
                toolTipOverlay.setClickable(false);
                break;
            default:
                break;
        }

    }

    private PopupMenu getMenu() {
        PopupMenu popup = new PopupMenu(getActivity(), personImage);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                menuItemClicked = true;
                return true;
            }
        });

        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu m) {
                if (!menuItemClicked) {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                }
                progressScene();
            }
        });
        popup.inflate(R.menu.person_menu);
        return popup;
    }

    private void setAllInvisible() {
        toolTipOverlay.setVisibility(View.INVISIBLE);

        toolTipOne.setVisibility(View.INVISIBLE);
        toolTipTwo.setVisibility(View.INVISIBLE);
        toolTipFour.setVisibility(View.INVISIBLE);

        favoriteTooltip.setVisibility(View.INVISIBLE);
        messageTooltip.setVisibility(View.INVISIBLE);
        addNoteTooltip.setVisibility(View.INVISIBLE);

    }

    public void onSelected() {
        if (showToolTip) {
            sceneNum = 0;
            menuItemClicked = false;
            setAllInvisible();
            setUpToolTip();
            animate();
        }
    }

}

