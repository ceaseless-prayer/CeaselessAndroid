package org.theotech.ceaselessandroid.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by UberEcks on 10/26/2015.
 * Contains methods that are shared between support fragments (for older api app compat) and the latest fragments.
 */
public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    public static Uri getContactUri(String contactId) {
        return ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
    }

    public static void injectPersonIntoView(final Activity activity, final PersonManager personManager, TextView personName,
                                            RoundedImageView personImage, ListView notes, View view, final String personId,
                                            String emptyNotesMessage, final FragmentManager fragmentManager, final FragmentState backStackInfo) {
        PersonPOJO personPOJO = personManager.getPerson(personId);
        if (null == personPOJO){
            return;
        }
        Uri contactUri = getContactUri(personPOJO.getId());

        // display name and picture
        personName.setText(personPOJO.getName());
        Picasso.get().load(contactUri).placeholder(R.drawable.placeholder_user)
                .fit().centerInside().into(personImage);

        TextView removedLabel = (TextView) view.findViewById(R.id.person_removed_label);
        if (personPOJO.isIgnored()) {
            removedLabel.setVisibility(View.VISIBLE);
        } else {
            removedLabel.setVisibility(View.INVISIBLE);
        }

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

    public static void wireFavoriteShortcut(final Activity activity, final View view, final String personId, final PersonManager personManager) {
        PersonPOJO personPOJO = personManager.getPerson(personId);
        final IconTextView favorite = (IconTextView) view.findViewById(R.id.favorite_btn);
        final String favoriteOn = "{fa-heart}";
        final String favoriteOff = activity.getString(R.string.favorite_off);

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

    public static void wireSendMessage(final Activity activity, View view, final String personId) {
        final IconTextView messageShortcut = (IconTextView) view.findViewById(R.id.message_btn);
        messageShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsUtils.sendEventWithCategory(activity,
                        activity.getString(R.string.ga_person_card_actions),
                        activity.getString(R.string.ga_tapped_send_message),
                        Installation.id(activity));
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(personId));
                ContactsContract.QuickContact.showQuickContact(activity, messageShortcut, contactUri, ContactsContract.QuickContact.MODE_MEDIUM, null);
            }
        });
    }

    public static void wireInvitePerson(final Activity activity, View view, final String personId) {
        final IconTextView inviteShortcut = (IconTextView) view.findViewById(R.id.invite_btn);
        inviteShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsUtils.sendEventWithCategory(activity,
                        activity.getString(R.string.ga_person_card_actions),
                        activity.getString(R.string.ga_tapped_invite),
                        Installation.id(activity));
                Uri contactUri1 = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(personId));
                ContactsContract.QuickContact.showQuickContact(activity, v, contactUri1, ContactsContract.QuickContact.MODE_MEDIUM, null);
            }
        });
    }

    public static void wireDeletePerson(final Activity activity, View view, final String personId, final PersonManager personManager) {
        PersonPOJO personPOJO = personManager.getPerson(personId);
        final IconTextView ignore = (IconTextView) view.findViewById(R.id.delete_btn);
        final TextView removedLabel = (TextView) view.findViewById(R.id.person_removed_label);
        final String ignoreOn = "{fa-trash}";
        final String ignoreOff = "{fa-trash-o}";

        // favorite
        if (personPOJO.isIgnored()) {
            ignore.setText(ignoreOn);
        } else {
            ignore.setText(ignoreOff);
        }
        ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonPOJO updatedPerson = personManager.getPerson(personId);
                if (updatedPerson.isIgnored()) {
                    personManager.unignorePerson(updatedPerson.getId());
                    ignore.setText(ignoreOff);
                    removedLabel.setVisibility(View.INVISIBLE);
                } else {
                    personManager.ignorePerson(updatedPerson.getId());
                    ignore.setText(ignoreOn);
                    removedLabel.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // wire the add note icon
    public static void wireAddNote(final View view, final String personId, final Activity activity, final FragmentState backStackInfo) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsUtils.sendEventWithCategory(activity,
                        activity.getString(R.string.ga_person_card_actions),
                        activity.getString(R.string.ga_tapped_add_note),
                        Installation.id(activity));
                CommonUtils.loadAddNote(personId, activity, activity.getFragmentManager(), backStackInfo);
            }
        });
    }

    public static void loadAddNote(String personId, Activity activity, FragmentManager fragmentManager, FragmentState backStackInfo) {
        Bundle addNoteBundle = new Bundle();
        addNoteBundle.putString(Constants.PERSON_ID_BUNDLE_ARG, personId);
        FragmentUtils.loadFragment(activity, fragmentManager, null, R.id.person_add_note, addNoteBundle, backStackInfo);
    }

    public static void wireShowPersonMenu(View view, final String personId, final Activity activity, final FragmentState backStackInfo, final PersonManager personManager) {
        final ImageView personImage = (ImageView) view.findViewById(R.id.person_image);
        final IconTextView favorite = (IconTextView) view.findViewById(R.id.favorite_btn);
        final TextView removedLabel = (TextView) view.findViewById(R.id.person_removed_label);

        personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(activity, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.person_fragment_add_note:
                                AnalyticsUtils.sendEventWithCategory(activity,
                                        activity.getString(R.string.ga_person_card_actions),
                                        activity.getString(R.string.ga_tapped_add_note),
                                        Installation.id(activity));
                                CommonUtils.loadAddNote(personId, activity, activity.getFragmentManager(), backStackInfo);
                                return true;
                            case R.id.person_fragment_remove:
                                personManager.ignorePerson(personId);
                                removedLabel.setVisibility(View.VISIBLE);
                                return true;
                            case R.id.person_fragment_add:
                                personManager.unignorePerson(personId);
                                removedLabel.setVisibility(View.INVISIBLE);
                                return true;
                            case R.id.person_fragment_send_message:
                                AnalyticsUtils.sendEventWithCategory(activity,
                                        activity.getString(R.string.ga_person_card_actions),
                                        activity.getString(R.string.ga_tapped_send_message),
                                        Installation.id(activity));
                                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(personId));
                                ContactsContract.QuickContact.showQuickContact(activity, v, contactUri, ContactsContract.QuickContact.MODE_MEDIUM, null);
                                return true;
                            case R.id.person_fragment_invite:
                                AnalyticsUtils.sendEventWithCategory(activity,
                                        activity.getString(R.string.ga_person_card_actions),
                                        activity.getString(R.string.ga_tapped_invite),
                                        Installation.id(activity));
                                Uri contactUri1 = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(personId));
                                ContactsContract.QuickContact.showQuickContact(activity, v, contactUri1, ContactsContract.QuickContact.MODE_MEDIUM, null);
                                return true;
                            case R.id.person_fragment_favorite:
                                favorite.setText(activity.getString(R.string.favorite_on));
                                personManager.favoritePerson(personId);
                                return true;
                            case R.id.person_fragment_unfavorite:
                                favorite.setText(activity.getString(R.string.favorite_off));
                                personManager.unfavoritePerson(personId);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.person_menu);
                Menu menu = popup.getMenu();
                PersonPOJO updatedPerson = personManager.getPerson(personId);

                if (updatedPerson.isIgnored()) {
                    menu.removeItem(R.id.person_fragment_remove);
                } else {
                    menu.removeItem(R.id.person_fragment_add);
                }

                if (updatedPerson.isFavorite()) {
                    menu.removeItem(R.id.person_fragment_favorite);
                } else {
                    menu.removeItem(R.id.person_fragment_unfavorite);
                }

                popup.show();
            }
        });
    }

    public static void setDynamicImage(Context context, ImageView target) {
        File currentBackgroundImage = new File(context.getCacheDir(), Constants.CURRENT_BACKGROUND_IMAGE);
        List<Transformation> transformations = new ArrayList<>();
        transformations.add(new BlurTransformation(context, 25, 4));

        if (currentBackgroundImage.exists()) {
            Picasso.get().load(currentBackgroundImage)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .fit()
                    .centerCrop()
                    .transform(transformations)
                    .into(target);
        } else {
            Picasso.get().load(R.drawable.at_the_beach)
                    .placeholder(R.drawable.placeholder_rectangle_scene)
                    .fit()
                    .centerCrop()
                    .transform(transformations)
                    .into(target);
        }
    }

    public static void setupBackgroundImage(Context context, ImageView target) {
        File currentBackgroundImage = new File(context.getCacheDir(), Constants.CURRENT_BACKGROUND_IMAGE);
        if (currentBackgroundImage.exists()) {
            Picasso.get()
                    .load(currentBackgroundImage)
                    .transform(new BlurTransformation(context, 25, 3))
                    .into(target);
            Log.d(TAG, "Background image has been set to " + currentBackgroundImage);
        } else {
            Log.d(TAG, "Showing default background image");
            Picasso.get()
                    .load(R.drawable.at_the_beach)
                    .transform(new BlurTransformation(context, 25, 3))
                    .into(target);
        }
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

    public static void hideKeyboard(Activity activity) {
        // hide keyboard if it's open
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
