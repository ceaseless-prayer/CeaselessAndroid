package org.theotech.ceaselessandroid.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chrislim on 1/14/16.
 */
public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = SearchResultsActivity.class.getSimpleName();
    PersonManager personManager;
    NoteManager noteManager;
    RecyclerView recyclerView;

    @BindView(R.id.backgroundImageView)
    ImageView backgroundImageView;
    @BindView(R.id.search_toolbar)
    Toolbar search_toolbar;
    @BindView(R.id.search_back_btn)
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

            final ConcatAdapter concatAdapter = new ConcatAdapter(
                    new PeopleSearchArrayAdapter(SearchResultsActivity.this, people),
                    new NotesSearchArrayAdapter(SearchResultsActivity.this, notes));
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setAdapter(concatAdapter);
        }
    }

    private class PeopleSearchArrayAdapter extends RecyclerView.Adapter<PeopleSearchArrayAdapter.PeopleSearchViewHolder> {
        private final Context context;
        private final List<PersonPOJO> persons;
        private final LayoutInflater inflater;

        public PeopleSearchArrayAdapter(Context context, List<PersonPOJO> persons)  {
            this.context = context;
            this.persons = persons;
            this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public PeopleSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PeopleSearchViewHolder holder;
            View view = inflater.inflate(R.layout.list_item_people_active, parent, false);
            holder = new PeopleSearchViewHolder(view);
            holder.favorite = view.findViewById(R.id.person_active_favorite);
            holder.favorite.setVisibility(View.GONE);
            holder.personThumbnail = view.findViewById(R.id.person_active_thumbnail);
            holder.personListName = view.findViewById(R.id.person_active_list_name);
            parent.setOnClickListener(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull PeopleSearchViewHolder holder, int position) {
            final PersonPOJO person = personManager.getPerson(persons.get(position).getId());
            // thumbnail picture
            Picasso.get()
                    .load(CommonUtils.getContactUri(person.getId()))
                    .placeholder(R.drawable.placeholder_user)
                    .fit().into(holder.personThumbnail);
            // person name
            holder.personListName.setText(person.getName());
        }

        @Override
        public int getItemCount() {
            return persons.size();
        }

        @Override
        public int getItemViewType (int position) {
            return R.layout.list_item_people_active;
        }

        class PeopleSearchViewHolder extends ViewHolder implements View.OnClickListener {
            IconTextView favorite;
            RoundedImageView personThumbnail;
            TextView personListName;

            public PeopleSearchViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                PersonPOJO person = (PersonPOJO) persons.get(getBindingAdapterPosition());
                bundle.putString(Constants.PERSON_ID_BUNDLE_ARG, person.getId());
                Intent intent = new Intent(Constants.SHOW_PERSON_INTENT);
                intent.putExtras(bundle);
            }
        }
    }

    private class NotesSearchArrayAdapter extends RecyclerView.Adapter<NotesSearchArrayAdapter.NotesSearchViewHolder> {
        private final Context context;
        private final List<NotePOJO> notes;
        private final LayoutInflater inflater;

        public NotesSearchArrayAdapter(Context context, List<NotePOJO> notes) {
            this.context = context;
            this.notes = notes;
            this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public NotesSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            NotesSearchViewHolder holder;
            View view = inflater.inflate(R.layout.list_item_notes, parent, false);
            holder = new NotesSearchViewHolder(view);
            holder.noteDate = view.findViewById(R.id.note_date);
            holder.noteText = view.findViewById(R.id.note_text);
            holder.notePeopleTagged = view.findViewById(R.id.note_people_tagged);
            holder.thumbnail1 = view.findViewById(R.id.person_tagged_thumbnail_1);
            holder.thumbnail2 = view.findViewById(R.id.person_tagged_thumbnail_2);
            parent.setOnClickListener(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull NotesSearchViewHolder holder, int position) {
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
                    Picasso.get()
                            .load(CommonUtils.getContactUri(note.getPeopleTagged().get(0)))
                            .placeholder(R.drawable.placeholder_user)
                            .fit()
                            .into(holder.thumbnail1);
                }

                if (peopleTagged.size() > 1) {
                    Picasso.get()
                            .load(CommonUtils.getContactUri(note.getPeopleTagged().get(1)))
                            .placeholder(R.drawable.placeholder_user)
                            .fit()
                            .into(holder.thumbnail2);
                } else {
                    holder.thumbnail2.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        @Override
        public int getItemViewType (int position) {
            return R.layout.list_item_notes;
        }

        class NotesSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView notePeopleTagged;
            TextView noteDate;
            TextView noteText;
            RoundedImageView thumbnail1;
            RoundedImageView thumbnail2;

            public NotesSearchViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                NotePOJO note = (NotePOJO) notes.get(getBindingAdapterPosition());
                bundle.putString(Constants.NOTE_ID_BUNDLE_ARG, note.getId());
                Intent intent = new Intent(Constants.SHOW_NOTE_INTENT);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }
}
