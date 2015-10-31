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
import org.theotech.ceaselessandroid.util.ListRefreshable;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PeopleRemovedSupportFragment extends Fragment implements ListRefreshable {
    private static final String TAG = PeopleRemovedSupportFragment.class.getSimpleName();

    @Bind(R.id.people_removed)
    ListView peopleRemoved;

    private PersonManager personManager;
    private RemovedPeopleArrayAdapter adapter;

    public PeopleRemovedSupportFragment() {
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
        View view = inflater.inflate(R.layout.fragment_support_people_removed, container, false);
        ButterKnife.bind(this, view);

        // populate the list of removed people
        final List<PersonPOJO> removedPersons = personManager.getRemovedPeople();
        adapter = new RemovedPeopleArrayAdapter(getActivity(), removedPersons);
        peopleRemoved.setAdapter(adapter);
        peopleRemoved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PERSON_ID_BUNDLE_ARG, removedPersons.get(position).getId());
                FragmentUtils.loadFragment(getActivity(), getActivity().getFragmentManager(), null,
                        R.id.person_card, bundle, new FragmentState(getString(R.string.nav_people)));
            }
        });

        return view;
    }

    @Override
    public void refreshList() {
        adapter.refresh();
    }

    private class RemovedPeopleArrayAdapter extends ArrayAdapter<PersonPOJO> {
        private final Context context;
        private final List<PersonPOJO> persons;
        private LayoutInflater inflater;

        public RemovedPeopleArrayAdapter(Context context, List<PersonPOJO> persons) {
            super(context, -1, persons);
            this.context = context;
            this.persons = persons;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_item_people_removed, parent, false);
                holder.personThumbnail = (ImageView) view.findViewById(R.id.person_removed_thumbnail);
                holder.personListName = (TextView) view.findViewById(R.id.person_removed_list_name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final PersonPOJO person = personManager.getPerson(persons.get(position).getId());
            // thumbnail picture
            Uri thumbnailUri = CommonUtils.getContactPhotoUri(context.getContentResolver(), person.getId(), false);
            Picasso.with(context).load(thumbnailUri).placeholder(R.drawable.placeholder_user).fit().into(holder.personThumbnail);
            // person name
            holder.personListName.setText(person.getName());

            return view;
        }

        public void refresh() {
            persons.clear();
            persons.addAll(personManager.getRemovedPeople());
            notifyDataSetChanged();
        }

        public void remove(String personId) {
            int index = -1;
            for (int i = 0; i < persons.size(); i++) {
                if (persons.get(i).getId().equals(personId)) {
                    index = i;
                }
            }
            if (index != -1) {
                persons.remove(index);
                notifyDataSetChanged();
            }
        }

        private class ViewHolder {
            ImageView personThumbnail;
            TextView personListName;
        }
    }
}
