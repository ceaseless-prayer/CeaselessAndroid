package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.tokenautocomplete.TokenCompleteTextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.view.PersonsCompletionView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddNoteFragment extends Fragment {

    @Bind(R.id.note_tags)
    PersonsCompletionView noteTags;
    @Bind(R.id.add_note_text)
    EditText noteText;
    @Bind(R.id.cancel_note)
    Button cancelNote;
    @Bind(R.id.save_note)
    Button saveNote;

    private List<PersonPOJO> taggedPeople;
    private PersonManager personManager = null;
    private CacheManager cacheManager = null;

    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        taggedPeople = new ArrayList<>();
        personManager = PersonManagerImpl.getInstance(getActivity());
        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set title
        getActivity().setTitle(getString(R.string.person_add_note));

        // create view and bind
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);
        ButterKnife.bind(this, view);

        // add current person to the list of taggedPeople (if we're on a page that shows a person)
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.PERSON_ID_BUNDLE_ARG)) {
            String personId = bundle.getString(Constants.PERSON_ID_BUNDLE_ARG);
            if (personId != null) {
                noteTags.addObject(personManager.getPerson(personId));
            }
        }

        // wire the note tags
        List<PersonPOJO> allPersonPOJOs = personManager.getActivePeople();
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
                hideKeyboard();

                // add the note to all the tagged people
                for (PersonPOJO taggedPerson : taggedPeople) {
                    personManager.addNote(taggedPerson.getId(), null, noteText.getText().toString());
                }

                // simulate back button press to exit this fragment
                getActivity().onBackPressed();
            }
        });
        cancelNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                // simulate back button press to exit this fragment
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void hideKeyboard() {
        // hide keyboard if it's open
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
