package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.tokenautocomplete.TokenCompleteTextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.view.PersonsCompletionView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddNoteFragment extends Fragment {

    @Bind(R.id.note_tags)
    PersonsCompletionView noteTags;
    @Bind(R.id.note_text)
    EditText noteText;
    @Bind(R.id.cancel_note)
    Button cancelNote;
    @Bind(R.id.save_note)
    Button saveNote;

    private List<PersonPOJO> taggedPeople;
    private PersonManager personManager = null;

    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        taggedPeople = new ArrayList<>();
        personManager = PersonManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.person_add_note));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);
        ButterKnife.bind(this, view);

        // wire the note tags
        List<PersonPOJO> allPersonPOJOs = personManager.getAllPeople();
        noteTags.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, allPersonPOJOs));
        noteTags.setTokenListener(new TokenCompleteTextView.TokenListener<PersonPOJO>() {
            @Override
            public void onTokenAdded(PersonPOJO token) {
                taggedPeople.add(token);
            }

            @Override
            public void onTokenRemoved(PersonPOJO token) {
                taggedPeople.remove(token);
            }
        });

        // wire the buttons
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (PersonPOJO taggedPerson : taggedPeople) {
                    personManager.addNote(taggedPerson.getId(), null, noteText.getText().toString());
                }
                getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();
            }
        });

        return view;
    }

}
