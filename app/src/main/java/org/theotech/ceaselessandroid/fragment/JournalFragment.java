package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.note.NoteManager;
import org.theotech.ceaselessandroid.note.NoteManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JournalFragment extends Fragment {

    @Bind(R.id.journal)
    ListView journal;
    private FragmentStateListener mListener;
    private NoteManager noteManager = null;

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
        List<NotePOJO> notePOJOs = noteManager.getNotes();
        // TODO take care of this in the realm query?
        Collections.sort(notePOJOs, new Comparator<NotePOJO>() { // sort by latest first
            @Override
            public int compare(NotePOJO lhs, NotePOJO rhs) {
                return -1 * lhs.getLastUpdatedDate().compareTo(rhs.getLastUpdatedDate());
            }
        });
        journal.setAdapter(new NotesArrayAdapter(getActivity(), notePOJOs));

        return view;
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
                holder.noteDate = (TextView) view.findViewById(R.id.note_date);
                holder.noteText = (TextView) view.findViewById(R.id.note_text);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            NotePOJO note = notes.get(position);
            // note date
            holder.noteDate.setText(note.getLastUpdatedDate().toString());
            // note text
            holder.noteText.setText(note.getText());

            return view;
        }

        private class ViewHolder {
            TextView noteDate;
            TextView noteText;
        }
    }

}
