package org.theotech.ceaselessandroid.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by UberEcks on 10/26/2015.
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

    public static void displayPerson(Activity activity, PersonManager personManager, TextView personName,
                                     ImageView personImage, LinearLayout notes, View view, String personId, String emptyNotesMessage) {
        PersonPOJO personPOJO = personManager.getPerson(personId);
        Uri personPhotoUri = CommonUtils.getContactPhotoUri(activity.getContentResolver(), personPOJO.getId(), true);

        personName.setText(personPOJO.getName());
        Picasso.with(activity).load(personPhotoUri).placeholder(R.drawable.placeholder_user).fit().centerInside().into(personImage);

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
        } else {
            for (int i = 0; i < notePOJOs.size(); i++) {
                View row = activity.getLayoutInflater().inflate(R.layout.list_item_person_notes, null);
                TextView noteDate = (TextView) row.findViewById(R.id.note_date);
                TextView noteText = (TextView) row.findViewById(R.id.note_text);
                noteDate.setText(notePOJOs.get(i).getLastUpdatedDate().toString());
                noteText.setText(notePOJOs.get(i).getText());
                notes.addView(row);
            }
        }
    }
}
