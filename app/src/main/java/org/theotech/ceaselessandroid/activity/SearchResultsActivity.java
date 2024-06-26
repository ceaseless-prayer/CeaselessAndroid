package org.theotech.ceaselessandroid.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.Date;
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

    ListAdapter mPeopleSearchAdapter;
    ListAdapter mNotesSearchAdapter;

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.ACTIVITY_SEARCH && resultCode == Constants.RESULT_NOTE_EDITED) {
            int notePos = data.getIntExtra(Constants.NOTE_POSITION_BUNDLE_ARG, -1);
            NotePOJO editedNote = (NotePOJO) mNotesSearchAdapter.getCurrentList().get(notePos);
            editedNote.setText(data.getStringExtra(Constants.NOTE_TEXT_BUNDLE_ARG));
            editedNote.setLastUpdatedDate(new Date());
            mNotesSearchAdapter.notifyItemChanged(notePos);
        }
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

            mPeopleSearchAdapter = new PeopleSearchArrayAdapter(new PeopleDiffCallback());
            mPeopleSearchAdapter.submitList(people);
            mNotesSearchAdapter = new NotesSearchArrayAdapter(new NotesDiffCallback());
            mNotesSearchAdapter.submitList(notes);
            final ConcatAdapter concatAdapter = new ConcatAdapter(
                    mPeopleSearchAdapter,
                    mNotesSearchAdapter);

            TextView searchEmptyView = findViewById(R.id.search_empty);
            RecyclerView searchResultsView = findViewById(R.id.search_results);
            searchResultsView.setAdapter(concatAdapter);


            if(concatAdapter.getItemCount() == 0) {
                searchEmptyView.setVisibility(View.VISIBLE);
                searchResultsView.setVisibility(View.GONE);
            } else {
                searchEmptyView.setVisibility(View.GONE);
                searchResultsView.setVisibility(View.VISIBLE);
            }
        }
    }
}
