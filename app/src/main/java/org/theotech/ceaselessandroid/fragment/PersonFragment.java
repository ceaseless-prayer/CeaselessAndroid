package org.theotech.ceaselessandroid.fragment;


import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.person.Person;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {
    private static final String TAG = PersonFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";

    private CacheManager cacheManager;

    public PersonFragment() {
        // Required empty public constructor
        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
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
        int index = getArguments().getInt(ARG_SECTION_NUMBER);
        // get data from cache
        Person person = cacheManager.getCachedPeopleToPrayFor().get(0);
        Uri personPhotoUri = getPhotoUri(person.getId());

        TextView personName = (TextView) view.findViewById(R.id.person_name);
        personName.setText(person.getName());
        final ImageView personImage = (ImageView) view.findViewById(R.id.person_image);
        Callback callback = new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(getActivity()).load(R.drawable.icon_76_2x).fit().centerCrop().into(personImage);
            }
        };
        if (personPhotoUri != null) {
            Picasso.with(getActivity()).load(personPhotoUri).fit().centerCrop().into(personImage, callback);
        } else {
            Picasso.with(getActivity()).load(R.drawable.icon_76_2x).fit().centerCrop().into(personImage);
        }

        ListView notes = (ListView) view.findViewById(R.id.person_notes);
        notes.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                new String[]{"Pray that this person truly seeks the will of God in their life. Pray " +
                        "that they would get better soon and be able to go to work without any interruptions.",
                        "Mid-terms coming up. Mom is not doing well. Has a missions trip coming up soon. Pray " +
                                "that they find their joy in Jesus."}));

        return view;
    }

    /**
     * @return the contact's photo URI
     */
    private Uri getPhotoUri(String personId) {
        try {
            Cursor cur = getActivity().getApplicationContext().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + personId + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    cur.close();
                    return null; // no photo
                }
                cur.close();
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(personId));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }


}
