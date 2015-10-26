package org.theotech.ceaselessandroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonSupportFragment extends Fragment {
    private static final String TAG = PersonSupportFragment.class.getSimpleName();

    @Bind(R.id.person_name)
    TextView personName;
    @Bind(R.id.person_image)
    ImageView personImage;
    @Bind(R.id.person_notes_list)
    LinearLayout notes;

    private boolean useCache;
    private CacheManager cacheManager;
    private PersonManager personManager;

    public PersonSupportFragment() {
        // Required empty public constructor
    }

    public static PersonSupportFragment newInstance(int sectionNumber) {
        PersonSupportFragment fragment = new PersonSupportFragment();
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
        View view = inflater.inflate(R.layout.fragment_support_person, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(Constants.PERSON_SECTION_NUMBER_BUNDLE_ARG)) {
                int index = bundle.getInt(Constants.PERSON_SECTION_NUMBER_BUNDLE_ARG);
                // people to pray for
                List<String> personIds = cacheManager.getCachedPersonIdsToPrayFor();
                if (personIds != null) {
                    String personId = personIds.get(index);
                    CommonUtils.displayPerson(getActivity(), personManager, personName, personImage, notes, view, personId, getString(R.string.empty_notes));
                }
            } else if (bundle.containsKey(Constants.PERSON_ID_BUNDLE_ARG)) {
                String personId = bundle.getString(Constants.PERSON_ID_BUNDLE_ARG);
                CommonUtils.displayPerson(getActivity(), personManager, personName, personImage, notes, view, personId, getString(R.string.empty_notes));
            }
        }
        return view;
    }
}
