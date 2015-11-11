package org.theotech.ceaselessandroid.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by UberEcks on 10/26/2015.
 * Contains methods that are shared between support fragments (for older api app compat) and the latest fragments.
 */
public class CommonUtils {

    public static Uri getContactPhotoUri(ContentResolver cr, String contactId, boolean highRes) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        if (highRes) {
            return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        } else {
            return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        }
    }

    public static void injectPersonIntoView(final Activity activity, final PersonManager personManager, TextView personName,
                                            ImageView personImage, LinearLayout notes, View view, final String personId,
                                            String emptyNotesMessage, final FragmentManager fragmentManager, final FragmentState backStackInfo) {
        PersonPOJO personPOJO = personManager.getPerson(personId);
        Uri personPhotoUri = CommonUtils.getContactPhotoUri(activity.getContentResolver(), personPOJO.getId(), true);

        // display name and picture
        personName.setText(personPOJO.getName());
        Picasso.with(activity).load(personPhotoUri).placeholder(R.drawable.placeholder_user)
                .fit().centerInside().into(personImage);

        // display notes
        List<NotePOJO> notePOJOs = personPOJO.getNotes();
        Collections.sort(notePOJOs, new Comparator<NotePOJO>() { // sort by latest first
            @Override
            public int compare(NotePOJO lhs, NotePOJO rhs) {
                return -1 * lhs.getLastUpdatedDate().compareTo(rhs.getLastUpdatedDate());
            }
        });
        if (notePOJOs.isEmpty()) {
            ListView emptyNotes = (ListView) view.findViewById(R.id.empty_notes);
            emptyNotes.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item_empty_notes, new String[]{emptyNotesMessage}));
            emptyNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    loadAddNote(personId, activity, fragmentManager, backStackInfo);
                }
            });
        } else {
            for (int i = 0; i < notePOJOs.size(); i++) {
                View row = activity.getLayoutInflater().inflate(R.layout.list_item_person_notes,
                                                                notes, false);
                TextView noteDate = (TextView) row.findViewById(R.id.person_note_date);
                TextView noteText = (TextView) row.findViewById(R.id.person_note_text);
                noteDate.setText(notePOJOs.get(i).getLastUpdatedDate().toString());
                noteText.setText(notePOJOs.get(i).getText());
                notes.addView(row);
            }
        }
    }

    public static void wireFavoriteShortcut(final View view, final String personId, final PersonManager personManager, final String favoriteOn, final String favoriteOff) {

        PersonPOJO personPOJO = personManager.getPerson(personId);
        final IconTextView favorite = (IconTextView) view.findViewById(R.id.favorite_btn);

        // favorite
        if (personPOJO.isFavorite()) {
            favorite.setText(favoriteOn);
        } else {
            favorite.setText(favoriteOff);
        }
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonPOJO updatedPerson = personManager.getPerson(personId);
                if (updatedPerson.isFavorite()) {
                    personManager.unfavoritePerson(updatedPerson.getId());
                    favorite.setText(favoriteOff);
                } else {
                    personManager.favoritePerson(updatedPerson.getId());
                    favorite.setText(favoriteOn);
                }
            }
        });
    }

    public static void wireSendMessage(final Context context, View view, final String personId) {
        final IconTextView messageShortcut = (IconTextView) view.findViewById(R.id.message_btn);
        messageShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(personId));
                ContactsContract.QuickContact.showQuickContact(context, messageShortcut, contactUri, ContactsContract.QuickContact.MODE_MEDIUM, null);
            }
        });
    }

    public static void loadAddNote(String personId, Activity activity, FragmentManager fragmentManager, FragmentState backStackInfo) {
        Bundle addNoteBundle = new Bundle();
        addNoteBundle.putString(Constants.PERSON_ID_BUNDLE_ARG, personId);
        FragmentUtils.loadFragment(activity, fragmentManager, null, R.id.person_add_note, addNoteBundle, backStackInfo);
    }
}
