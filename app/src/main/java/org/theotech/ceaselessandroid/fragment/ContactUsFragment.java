package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.theotech.ceaselessandroid.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    @Bind(R.id.contact_us_button)
    TextView contactUsButton;

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set title
        getActivity().setTitle(getString(R.string.nav_contact_us));

        // create view and bind
        final View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
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
            // TODO
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


}
