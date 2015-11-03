package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

    private FragmentStateListener mListener;
    private NoteManager noteManager = null;

    @Bind(R.id.journal_note_list)
    LinearLayout notes;

    public JournalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // fragment listener
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

        if (notePOJOs.isEmpty()) {
            ListView emptyNotes = (ListView) view.findViewById(R.id.empty_notes);
            emptyNotes.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_item_empty_notes, new String[]{getString(R.string.empty_notes)}));
            emptyNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO loadAddNote(personId, activity, fragmentManager, backStackInfo);
                }
            });
        } else {
            for (int i = 0; i < notePOJOs.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.list_item_person_notes, null);
                TextView noteDate = (TextView) row.findViewById(R.id.note_date);
                TextView noteText = (TextView) row.findViewById(R.id.note_text);
                noteDate.setText(notePOJOs.get(i).getLastUpdatedDate().toString());
                noteText.setText(notePOJOs.get(i).getText());
                notes.addView(row);
            }
        }

        return view;
    }

}
