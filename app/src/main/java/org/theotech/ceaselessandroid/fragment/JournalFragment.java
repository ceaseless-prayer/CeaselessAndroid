package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.CeaselessApplication;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.note.NoteManager;
import org.theotech.ceaselessandroid.note.NoteManagerImpl;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.util.AnalyticsUtils;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.FragmentUtils;
import org.theotech.ceaselessandroid.util.Refreshable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalFragment extends Fragment implements Refreshable {

    private static final String TAG = JournalFragment.class.getSimpleName();

    @BindView(R.id.journal)
    ListView journal;
    @BindView(R.id.empty_journal_notes)
    TextView emptyNotes;
    private FragmentStateListener mListener;
    private NoteManager noteManager = null;
    private PersonManager personManager = null;
    private ActionMode actionMode;
    private NotesArrayAdapter adapter;

    public JournalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // notify fragment state
        try {
            mListener = (FragmentStateListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FragmentStateListener");
        }

        mListener.notify(new FragmentState(getString(R.string.nav_journal)));

        noteManager = NoteManagerImpl.getInstance(getActivity());
        personManager = PersonManagerImpl.getInstance(getActivity());

        CeaselessApplication application = (CeaselessApplication) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.nav_journal));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
        ButterKnife.bind(this, view);

        // display notes
        final List<NotePOJO> notePOJOs = noteManager.getNotes();

        if (notePOJOs.isEmpty()) {
            emptyNotes.setText(getString(R.string.empty_notes));
        } else {
            emptyNotes.setText("");
            adapter = new NotesArrayAdapter(getActivity(), notePOJOs);
            journal.setAdapter(adapter);

            journal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.NOTE_ID_BUNDLE_ARG, notePOJOs.get(position).getId());
                    FragmentUtils.loadFragment(getActivity(), getActivity().getFragmentManager(), null,
                            R.id.add_note_fragment, bundle, new FragmentState(getString(R.string.nav_journal)));
                }
            });

            journal.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            journal.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    final int checkedCount = journal.getCheckedItemCount();
                    mode.setTitle(String.format(getString(R.string.bulk_selected), checkedCount));
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    actionMode = mode;
                    mode.getMenuInflater().inflate(R.menu.journal_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.note_remove) {
                        final List<NotePOJO> notes = noteManager.getNotes();
                        SparseBooleanArray array = journal.getCheckedItemPositions();
                        for (int i = 0; i < array.size(); i++) {
                            int position = array.keyAt(i);
                            NotePOJO note = notes.get(position);
                            noteManager.removeNote(note.getId());
                            adapter.remove(note.getId());
                        }
                        if (adapter.size() == 0) {
                            emptyNotes.setText(getString(R.string.empty_notes));
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
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.sendScreenViewHit(this.getActivity(), "JournalScreen");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.NOTE_ID_BUNDLE_ARG)) {
            FragmentUtils.loadFragment(getActivity(), getActivity().getFragmentManager(), null,
                    R.id.add_note_fragment, bundle, new FragmentState(getString(R.string.nav_journal)));
        }
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

    private class NotesArrayAdapter extends ArrayAdapter<NotePOJO> {
        private final Context context;
        private final List<NotePOJO> notes;
        private final LayoutInflater inflater;

        public NotesArrayAdapter(Context context, List<NotePOJO> notes) {
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
                holder.noteDate = view.findViewById(R.id.note_date);
                holder.noteText = view.findViewById(R.id.note_text);
                holder.notePeopleTagged = view.findViewById(R.id.note_people_tagged);
                holder.thumbnail1 = view.findViewById(R.id.person_tagged_thumbnail_1);
                holder.thumbnail2 = view.findViewById(R.id.person_tagged_thumbnail_2);
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

        public void refresh() {
            notes.clear();
            notes.addAll(noteManager.getNotes());
            notifyDataSetChanged();
        }

        public void remove(String noteId) {
            Log.d(TAG, "removing note " + noteId);
            int index = -1;
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getId().equals(noteId)) {
                    index = i;
                }
            }
            if (index != -1) {
                notes.remove(index);
                notifyDataSetChanged();
            }
        }

        public int size() {
            return notes.size();
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
