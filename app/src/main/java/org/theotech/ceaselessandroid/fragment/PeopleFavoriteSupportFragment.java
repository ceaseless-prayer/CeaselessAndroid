package org.theotech.ceaselessandroid.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.FragmentUtils;
import org.theotech.ceaselessandroid.util.Refreshable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeopleFavoriteSupportFragment extends Fragment implements Refreshable {
    private static final String TAG = PeopleFavoriteSupportFragment.class.getSimpleName();

    @BindView(R.id.people_favorite)
    ListView peopleFavorite;

    private PersonManager personManager;
    private FavoritePeopleArrayAdapter adapter;
    private ActionMode actionMode;

    public PeopleFavoriteSupportFragment() {
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
        View view = inflater.inflate(R.layout.fragment_support_people_favorite, container, false);
        ButterKnife.bind(this, view);

        // populate the list of favorite people
        final List<PersonPOJO> favoritePersons = personManager.getFavoritePeople();
        adapter = new FavoritePeopleArrayAdapter(getActivity(), favoritePersons);
        peopleFavorite.setAdapter(adapter);
        peopleFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PERSON_ID_BUNDLE_ARG, favoritePersons.get(position).getId());
                FragmentUtils.loadFragment(getActivity(), getActivity().getFragmentManager(), null,
                        R.id.person_card, bundle, new FragmentState(getString(R.string.nav_people)));
            }
        });
        peopleFavorite.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        peopleFavorite.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = peopleFavorite.getCheckedItemCount();
                mode.setTitle(String.format(getString(R.string.bulk_selected), checkedCount));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
                mode.getMenuInflater().inflate(R.menu.person_favorite_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.person_unfavorite) {
                    final List<PersonPOJO> persons = personManager.getFavoritePeople();
                    SparseBooleanArray array = peopleFavorite.getCheckedItemPositions();
                    for (int i = 0; i < array.size(); i++) {
                        int position = array.keyAt(i);
                        PersonPOJO person = persons.get(position);
                        personManager.unfavoritePerson(person.getId());
                        adapter.remove(person.getId());
                    }
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        });

        return view;
    }

    @Override
    public void refreshList() {
        adapter.refresh();
    }

    @Override
    public void dismissActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    private class FavoritePeopleArrayAdapter extends ArrayAdapter<PersonPOJO> {
        private final Context context;
        private final List<PersonPOJO> persons;
        private final LayoutInflater inflater;

        public FavoritePeopleArrayAdapter(Context context, List<PersonPOJO> persons) {
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
                view = inflater.inflate(R.layout.list_item_people_favorite, parent, false);
                holder.personThumbnail = view.findViewById(R.id.person_favorite_thumbnail);
                holder.personListName = view.findViewById(R.id.person_favorite_list_name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final PersonPOJO person = personManager.getPerson(persons.get(position).getId());
            // thumbnail picture
            Picasso.get().load(CommonUtils.getContactUri(person.getId())).placeholder(R.drawable.placeholder_user).fit().into(holder.personThumbnail);
            // person name
            holder.personListName.setText(person.getName());

            return view;
        }

        public void refresh() {
            persons.clear();
            persons.addAll(personManager.getFavoritePeople());
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
            RoundedImageView personThumbnail;
            TextView personListName;
        }
    }
}
