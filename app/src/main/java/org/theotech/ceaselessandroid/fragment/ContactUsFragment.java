package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    @Bind(R.id.contact_us_button)
    TextView contactUsButton;
    @Bind(R.id.fb_image)
    ImageView fbImage;
    @Bind(R.id.twitter_image)
    ImageView twitterImage;

    private FragmentStateListener mListener;

    public ContactUsFragment() {
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
        mListener.notify(new FragmentState(getString(R.string.nav_contact_us)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_contact_us));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.bind(this, view);

        // click listener for contact us button
        View.OnClickListener contactUsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEmailIntent();
            }
        };

        // add click listener to contact us button
        contactUsButton.setOnClickListener(contactUsOnClickListener);

        Picasso.with(getActivity()).load(R.drawable.fb_blue_512).placeholder(R.drawable.placeholder_square_scene).fit().into(fbImage);

        // click listener for FB button
        fbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWebIntent("https://facebook.com/ceaselesspraying");
            }
        });

        Picasso.with(getActivity()).load(R.drawable.twitter_logo_844).placeholder(R.drawable.placeholder_square_scene).fit().into(twitterImage);

        // click listener for Twitter button
        twitterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWebIntent("https://twitter.com/ceaselessprayer");
            }
        });

        return view;
    }

    public void startEmailIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ceaseless@theotech.org"});

        // TODO localize
        i.putExtra(Intent.EXTRA_SUBJECT, "Ceaseless for Android Feedback");
        i.putExtra(Intent.EXTRA_TEXT, "Feedback for Ceaseless Android app: \n");

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void startWebIntent(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(browserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Unable to open web browser.", Toast.LENGTH_SHORT).show();
        }
    }


}
