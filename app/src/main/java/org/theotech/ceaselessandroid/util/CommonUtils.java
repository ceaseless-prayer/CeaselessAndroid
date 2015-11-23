package org.theotech.ceaselessandroid.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by UberEcks on 10/26/2015.
 * Contains methods that are shared between support fragments (for older api app compat) and the latest fragments.
 */
public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    public static Uri getContactPhotoUri(ContentResolver cr, String contactId, boolean highRes) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        Uri displayPhoto = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        Uri lowResPhoto = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        if (highRes) {
            // check existence of high resolution photo and return low res version otherwise.
            try {
                AssetFileDescriptor fd = cr.openAssetFileDescriptor(displayPhoto, "r");
                fd.close();
            } catch (IOException e) {
                return lowResPhoto;
            }
            return displayPhoto;
        } else {
            return lowResPhoto;
        }
    }

    public static void injectPersonIntoView(final Activity activity, final PersonManager personManager, TextView personName,
                                            RoundedImageView personImage, ListView notes, View view, final String personId,
                                            String emptyNotesMessage, final FragmentManager fragmentManager, final FragmentState backStackInfo) {
        PersonPOJO personPOJO = personManager.getPerson(personId);
        Uri personPhotoUri = CommonUtils.getContactPhotoUri(activity.getContentResolver(), personPOJO.getId(), true);

        // display name and picture
        personName.setText(personPOJO.getName());
        Picasso.with(activity).load(personPhotoUri).placeholder(R.drawable.placeholder_user)
                .fit().centerInside().into(personImage);

        // display notes
        final List<NotePOJO> notePOJOs = personPOJO.getNotes();
        Collections.sort(notePOJOs, new Comparator<NotePOJO>() { // sort by latest first
            @Override
            public int compare(NotePOJO lhs, NotePOJO rhs) {
                return -1 * lhs.getLastUpdatedDate().compareTo(rhs.getLastUpdatedDate());
            }
        });
        if (notePOJOs.isEmpty()) {
            ListView emptyNotes = (ListView) view.findViewById(R.id.empty_person_notes);
            emptyNotes.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item_empty_notes, new String[]{emptyNotesMessage}));
            emptyNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    loadAddNote(personId, activity, fragmentManager, backStackInfo);
                }
            });
        } else {
            notes.setAdapter(new PersonNotesArrayAdapter(activity, notePOJOs));
            notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "item has been clicked");
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.NOTE_ID_BUNDLE_ARG, notePOJOs.get(position).getId());
                    FragmentUtils.loadFragment(activity, activity.getFragmentManager(), null,
                            R.id.add_note_fragment, bundle, backStackInfo);
                }
            });
        }
    }

    public static void wireFavoriteShortcut(final View view, final String personId, final PersonManager personManager,
                                            final String favoriteOn, final String favoriteOff) {
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

    private static class PersonNotesArrayAdapter extends ArrayAdapter<NotePOJO> {
        private final Context context;
        private final List<NotePOJO> notes;
        private final LayoutInflater inflater;

        public PersonNotesArrayAdapter(Context context, List<NotePOJO> notes) {
            super(context, -1, notes);
            this.context = context;
            this.notes = notes;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_person_notes, parent, false);
                holder = new ViewHolder();
                holder.noteDate = (TextView) view.findViewById(R.id.person_note_date);
                holder.noteText = (TextView) view.findViewById(R.id.person_note_text);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            NotePOJO note = notes.get(position);
            DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
            holder.noteDate.setText(formatter.format(note.getLastUpdatedDate()));
            holder.noteText.setText(note.getText());
            return view;
        }

        private class ViewHolder {
            TextView noteDate;
            TextView noteText;
        }
    }

}
