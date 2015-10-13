package org.theotech.ceaselessandroid.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.Note;
import org.theotech.ceaselessandroid.realm.Person;
import org.theotech.ceaselessandroid.util.ActivityUtils;
import org.theotech.ceaselessandroid.util.Constants;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {
    private static final String TAG = PersonFragment.class.getSimpleName();

    private CacheManager cacheManager;
    private PersonManager personManager;

    public PersonFragment() {
        // Required empty public constructor
        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
        personManager = PersonManagerImpl.getInstance(getActivity());
    }

    public static PersonFragment newInstance(int sectionNumber) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.PERSON_ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        int index = getArguments().getInt(Constants.PERSON_ARG_SECTION_NUMBER);
        // get data from cache
        String personId = cacheManager.getCachedPersonIdsToPrayFor().get(index);
        Person person = personManager.getPerson(personId);
        Uri personPhotoUri = ActivityUtils.getContactPhotoUri(getActivity().getContentResolver(), person.getId(), true);

        TextView personName = (TextView) view.findViewById(R.id.person_name);
        personName.setText(person.getName());
        final ImageView personImage = (ImageView) view.findViewById(R.id.person_image);
        Picasso.with(getActivity()).load(personPhotoUri).placeholder(R.drawable.placeholder_user).fit().centerInside().into(personImage);

        ListView notes = (ListView) view.findViewById(R.id.person_notes);
        List<Note> personNotes = person.getNotes();
        String[] noteTexts = new String[personNotes.size()];
        for (int i = 0; i < personNotes.size(); i++) {
            noteTexts[i] = personNotes.get(i).getText();
        }
        notes.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, noteTexts));

        return view;
    }
}
