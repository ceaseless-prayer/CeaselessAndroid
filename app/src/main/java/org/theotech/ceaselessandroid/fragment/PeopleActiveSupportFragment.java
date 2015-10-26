package org.theotech.ceaselessandroid.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.FragmentUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PeopleActiveSupportFragment extends Fragment {
    private static final String TAG = PeopleActiveSupportFragment.class.getSimpleName();

    @Bind(R.id.people_active)
    ListView peopleActive;

    private PersonManager personManager;

    public PeopleActiveSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        personManager = PersonManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_people));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_support_people_active, container, false);
        ButterKnife.bind(this, view);

        // populate the list of active people
        final List<PersonPOJO> activePersons = personManager.getActivePeople();
        peopleActive.setAdapter(new ActivePeopleArrayAdapter(getActivity(), activePersons));
        peopleActive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PERSON_ID_BUNDLE_ARG, activePersons.get(position).getId());
                FragmentUtils.loadFragment(getActivity(), getActivity().getFragmentManager(), null,
                        R.id.person_card, bundle, new FragmentState(getString(R.string.nav_people)));
            }
        });

        return view;
    }

    private class ActivePeopleArrayAdapter extends ArrayAdapter<PersonPOJO> {
        private final Context context;
        private final List<PersonPOJO> persons;

        public ActivePeopleArrayAdapter(Context context, List<PersonPOJO> persons) {
            super(context, -1, persons);
            this.context = context;
            this.persons = persons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_people_active, parent, false);
            ImageView personThumbnail = (ImageView) rowView.findViewById(R.id.person_thumbnail);
            TextView personListName = (TextView) rowView.findViewById(R.id.person_list_name);
            PersonPOJO person = persons.get(position);
            // thumbnail picture
            Uri thumbnailUri = CommonUtils.getContactPhotoUri(context.getContentResolver(), person.getId(), false);
            Picasso.with(context).load(thumbnailUri).placeholder(R.drawable.placeholder_user).fit().into(personThumbnail);
            // person name
            personListName.setText(person.getName());

            return rowView;
        }
    }
}
