package org.theotech.ceaselessandroid.tutorial;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    TextView toolTipTwo;
    @Bind(R.id.tool_tip_three)
    LinearLayout toolTipThree;
    @Bind(R.id.tool_tip_four)
    LinearLayout toolTipFour;

    @Bind(R.id.up_arrow)
    IconTextView upArrow;
    @Bind(R.id.right_arrow)
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
        final long fadeDuration = 4000;
        final long startFadeTime = 1000;

        LinearInterpolator linInter = new LinearInterpolator();

        TranslateAnimation mHorAnimation = new TranslateAnimation(
                -3, 3, 0, 0);
        mHorAnimation.setDuration(150);
        mHorAnimation.setStartOffset(20);
        mHorAnimation.setRepeatCount(-1);
        mHorAnimation.setRepeatMode(Animation.REVERSE);
        mHorAnimation.setInterpolator(linInter);
        rightArrow.setAnimation(mHorAnimation);

        TranslateAnimation mVerAnimation = new TranslateAnimation(
                0, 0, -3, 3);
        mVerAnimation.setDuration(150);
        mVerAnimation.setStartOffset(20);
        mVerAnimation.setRepeatCount(-1);
        mVerAnimation.setRepeatMode(Animation.REVERSE);
        mVerAnimation.setInterpolator(linInter);
        upArrow.setAnimation(mVerAnimation);

        AlphaAnimation mAlAnimation = new AlphaAnimation(0, 1);
        mAlAnimation.setDuration(fadeDuration);
        mAlAnimation.setStartOffset(startFadeTime);
        mAlAnimation.setInterpolator(linInter);
        toolTipOverlay.setAnimation(mAlAnimation);
        toolTipOne.setAnimation(mAlAnimation);

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
            menuItemClicked = false;
            setAllInvisible();
            setUpToolTip();
            animate();
        }
    }

}

