package org.theotech.ceaselessandroid.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

import com.joanzapata.iconify.widget.IconTextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.adapters.NotesSearchArrayAdapter;
import org.theotech.ceaselessandroid.adapters.PeopleSearchArrayAdapter;
import org.theotech.ceaselessandroid.note.NoteManager;
import org.theotech.ceaselessandroid.note.NoteManagerImpl;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.NotesDiffCallback;
import org.theotech.ceaselessandroid.util.PeopleDiffCallback;

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

            ListAdapter peopleSearchAdapter = new PeopleSearchArrayAdapter(new PeopleDiffCallback());
            peopleSearchAdapter.submitList(people);
            ListAdapter notesSearchAdapter = new NotesSearchArrayAdapter(new NotesDiffCallback());
            notesSearchAdapter.submitList(notes);
            final ConcatAdapter concatAdapter = new ConcatAdapter(
                    peopleSearchAdapter,
                    notesSearchAdapter);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setAdapter(concatAdapter);
        }
    }
}
