package org.theotech.ceaselessandroid.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.ActivityUtils;
import org.theotech.ceaselessandroid.util.Constants;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonFragment extends Fragment {
    private static final String TAG = PersonFragment.class.getSimpleName();

    @Bind(R.id.person_name)
    TextView personName;
    @Bind(R.id.person_image)
    ImageView personImage;
    @Bind(R.id.person_notes_list)
    LinearLayout notes;

    private boolean useCache;
    private CacheManager cacheManager;
    private PersonManager personManager;

    public PersonFragment() {
        // Required empty public constructor
    }

    public static PersonFragment newInstance(int sectionNumber) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.PERSON_SECTION_NUMBER_BUNDLE_ARG, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.USE_CACHE_BUNDLE_ARG)) {
            this.useCache = bundle.getBoolean(Constants.USE_CACHE_BUNDLE_ARG);
        } else {
            this.useCache = true;
        }

        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
        personManager = PersonManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view and bind
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(Constants.PERSON_SECTION_NUMBER_BUNDLE_ARG)) {
                int index = bundle.getInt(Constants.PERSON_SECTION_NUMBER_BUNDLE_ARG);
                // people to pray for
                List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
                if (personIds != null) {
                    String personId = personIds.get(index);
                    displayPerson(view, personId);
                }
            } else if (bundle.containsKey(Constants.PERSON_ID_BUNDLE_ARG)) {
                String personId = bundle.getString(Constants.PERSON_ID_BUNDLE_ARG);
                displayPerson(view, personId);
            }
        }
        return view;
    }

    private void displayPerson(View view, String personId) {
        PersonPOJO personPOJO = personManager.getPerson(personId);
        Uri personPhotoUri = ActivityUtils.getContactPhotoUri(getActivity().getContentResolver(), personPOJO.getId(), true);

        personName.setText(personPOJO.getName());
        Picasso.with(getActivity()).load(personPhotoUri).placeholder(R.drawable.placeholder_user).fit().centerInside().into(personImage);

        List<NotePOJO> notePOJOs = personPOJO.getNotes();
        Collections.sort(notePOJOs, new Comparator<NotePOJO>() { // sort by latest first
            @Override
            public int compare(NotePOJO lhs, NotePOJO rhs) {
                return -1 * lhs.getLastUpdatedDate().compareTo(rhs.getLastUpdatedDate());
            }
        });
        if (notePOJOs.isEmpty()) {
            ListView emptyNotes = (ListView) view.findViewById(R.id.empty_notes);
            emptyNotes.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.empty_notes_list_item, new String[]{getString(R.string.empty_notes)}));
        } else {
            for (int i = 0; i < notePOJOs.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.person_notes_list_item, null);
                TextView noteDate = (TextView) row.findViewById(R.id.note_date);
                TextView noteText = (TextView) row.findViewById(R.id.note_text);
                noteDate.setText(notePOJOs.get(i).getLastUpdatedDate().toString());
                noteText.setText(notePOJOs.get(i).getText());
                notes.addView(row);
            }
        }
    }
}
