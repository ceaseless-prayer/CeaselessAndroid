package org.theotech.ceaselessandroid.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.google.common.base.Joiner;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.note.NoteManager;
import org.theotech.ceaselessandroid.note.NoteManagerImpl;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chrislim on 1/14/16.
 */
public class SearchResultsActivity extends ListActivity {

    private static final String TAG = SearchResultsActivity.class.getSimpleName();
    PersonManager personManager;
    NoteManager noteManager;

    @Bind(R.id.backgroundImageView)
    ImageView backgroundImageView;
    @Bind(R.id.search_toolbar)
    Toolbar search_toolbar;
    @Bind(R.id.search_back_btn)
    IconTextView search_back_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        personManager = PersonManagerImpl.getInstance(this);
        noteManager = NoteManagerImpl.getInstance(this);
        handleIntent(getIntent());
        CommonUtils.setupBackgroundImage(this, backgroundImageView);
        search_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchResultsActivity.this.finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            final List<PersonPOJO> people = personManager.queryPeopleByName(query);
            final List<NotePOJO> notes = noteManager.queryNotesByText(query);

            final MergeAdapter mergeAdapter = new MergeAdapter() {
                @Override
                public boolean isEmpty() {
                    return people.isEmpty() && notes.isEmpty();
                }
            };

            mergeAdapter.addAdapter(new PeopleSearchArrayAdapter(SearchResultsActivity.this, people));
            mergeAdapter.addAdapter(new NotesSearchArrayAdapter(SearchResultsActivity.this, notes));
            setListAdapter(mergeAdapter);

            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    if (mergeAdapter.getItem(position) instanceof PersonPOJO) {
                        PersonPOJO person = (PersonPOJO) mergeAdapter.getItem(position);
                        bundle.putString(Constants.PERSON_ID_BUNDLE_ARG, person.getId());
                        Intent intent = new Intent(Constants.SHOW_PERSON_INTENT);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (mergeAdapter.getItem(position) instanceof NotePOJO) {
                        NotePOJO note = (NotePOJO) mergeAdapter.getItem(position);
                        bundle.putString(Constants.NOTE_ID_BUNDLE_ARG, note.getId());
                        Intent intent = new Intent(Constants.SHOW_NOTE_INTENT);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

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
            Picasso.with(context).load(CommonUtils.getContactUri(person.getId())).placeholder(R.drawable.placeholder_user).fit().into(holder.personThumbnail);
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

    private class NotesSearchArrayAdapter extends ArrayAdapter<NotePOJO> {
        private final Context context;
        private final List<NotePOJO> notes;
        private final LayoutInflater inflater;

        public NotesSearchArrayAdapter(Context context, List<NotePOJO> notes) {
            super(context, -1, notes);
            this.context = context;
            this.notes = notes;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_item_notes, parent, false);
                holder.noteDate = (TextView) view.findViewById(R.id.note_date);
                holder.noteText = (TextView) view.findViewById(R.id.note_text);
                holder.notePeopleTagged = (TextView) view.findViewById(R.id.note_people_tagged);
                holder.thumbnail1 = (RoundedImageView) view.findViewById(R.id.person_tagged_thumbnail_1);
                holder.thumbnail2 = (RoundedImageView) view.findViewById(R.id.person_tagged_thumbnail_2);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            NotePOJO note = notes.get(position);
            DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
            holder.noteDate.setText(formatter.format(note.getLastUpdatedDate()));
            holder.noteText.setText(note.getText());
            holder.notePeopleTagged.setText(Joiner.on(", ").join(note.getPeopleTaggedNames()));

            List<String> peopleTagged = note.getPeopleTagged();
            if (peopleTagged == null || peopleTagged.size() == 0) {
                holder.notePeopleTagged.setVisibility(View.GONE);
                holder.thumbnail1.setVisibility(View.INVISIBLE);
                holder.thumbnail2.setVisibility(View.INVISIBLE);
            } else {
                holder.notePeopleTagged.setVisibility(View.VISIBLE);
                holder.thumbnail1.setVisibility(View.VISIBLE);
                holder.thumbnail2.setVisibility(View.VISIBLE);

                if (peopleTagged.size() > 0) {
                    Picasso.with(context)
                            .load(CommonUtils.getContactUri(note.getPeopleTagged().get(0)))
                            .placeholder(R.drawable.placeholder_user)
                            .fit()
                            .into(holder.thumbnail1);
                }

                if (peopleTagged.size() > 1) {
                    Picasso.with(context)
                            .load(CommonUtils.getContactUri(note.getPeopleTagged().get(1)))
                            .placeholder(R.drawable.placeholder_user)
                            .fit()
                            .into(holder.thumbnail2);
                } else {
                    holder.thumbnail2.setVisibility(View.INVISIBLE);
                }
            }

            return view;
        }

        private class ViewHolder {
            TextView notePeopleTagged;
            TextView noteDate;
            TextView noteText;
            RoundedImageView thumbnail1;
            RoundedImageView thumbnail2;
        }
    }
}
