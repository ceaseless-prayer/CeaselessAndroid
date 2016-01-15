package org.theotech.ceaselessandroid.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;

import java.util.List;

/**
 * Created by chrislim on 1/14/16.
 */
public class SearchResultsActivity extends ListActivity {

    private static final String TAG = SearchResultsActivity.class.getSimpleName();;
    PersonManager personManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        personManager = PersonManagerImpl.getInstance(this);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            final List<PersonPOJO> results = personManager.queryPeopleByName(query);
            // search for the person
            setListAdapter(new PeopleSearchArrayAdapter(SearchResultsActivity.this, results));
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PERSON_ID_BUNDLE_ARG, results.get(position).getId());
                    Intent intent = new Intent(Constants.SHOW_PERSON_INTENT);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    private class PeopleSearchArrayAdapter extends ArrayAdapter<PersonPOJO> {
        private final Context context;
        private final List<PersonPOJO> persons;
        private final LayoutInflater inflater;

        public PeopleSearchArrayAdapter(Context context, List<PersonPOJO> persons) {
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
                view = inflater.inflate(R.layout.list_item_people_active, parent, false);
                holder.favorite = (IconTextView) view.findViewById(R.id.person_active_favorite);
                holder.favorite.setVisibility(View.GONE);
                holder.personThumbnail = (RoundedImageView) view.findViewById(R.id.person_active_thumbnail);
                holder.personListName = (TextView) view.findViewById(R.id.person_active_list_name);
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

        private class ViewHolder {
            IconTextView favorite;
            RoundedImageView personThumbnail;
            TextView personListName;
        }
    }
}
