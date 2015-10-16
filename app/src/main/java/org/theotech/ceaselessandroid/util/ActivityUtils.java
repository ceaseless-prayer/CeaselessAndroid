package org.theotech.ceaselessandroid.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.util.Log;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.AddNoteFragment;
import org.theotech.ceaselessandroid.fragment.ContactUsFragment;
import org.theotech.ceaselessandroid.fragment.HelpFragment;
import org.theotech.ceaselessandroid.fragment.HomeFragment;
import org.theotech.ceaselessandroid.fragment.JournalFragment;
import org.theotech.ceaselessandroid.fragment.PeopleFragment;
import org.theotech.ceaselessandroid.fragment.SettingsFragment;

/**
 * Created by uberx on 10/8/15.
 */
public class ActivityUtils {
    private static final String TAG = ActivityUtils.class.getSimpleName();

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation, int resourceId) {
        loadFragment(activity, fragmentManager, null, navigation, resourceId);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation, int resourceId, boolean addToBackStack) {
        loadFragment(activity, fragmentManager, null, navigation, resourceId, addToBackStack);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, Bundle bundle, NavigationView navigation, int resourceId) {
        loadFragment(activity, fragmentManager, bundle, navigation, resourceId, true);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, Bundle bundle, NavigationView navigation, int resourceId, boolean addToBackStack) {
        Fragment fragment = getFragmentForResourceId(resourceId);
        String fragmentTag = getFragmentTagForResourceId(activity, resourceId);
        Fragment fragmentForTag = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null && (fragmentForTag == null || !fragmentForTag.isVisible())) {
            fragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction().replace(R.id.fragment, fragment, fragmentTag);
            if (addToBackStack) {
                transaction.addToBackStack(activity.getTitle().toString());
            }
            transaction.commit();
            navigation.setCheckedItem(getNavigationItemIdForFragmentName(activity, fragmentTag));
            activity.setTitle(fragmentTag);
        } else {
            Log.d(TAG, String.format("Required fragment %s already visible, not reloading", fragmentTag));
        }
    }

    private static Fragment getFragmentForResourceId(int resourceId) {
        if (resourceId == R.id.nav_home) {
            return new HomeFragment();
        } else if (resourceId == R.id.nav_people) {
            return new PeopleFragment();
        } else if (resourceId == R.id.nav_journal) {
            return new JournalFragment();
        } else if (resourceId == R.id.nav_settings) {
            return new SettingsFragment();
        } else if (resourceId == R.id.nav_help) {
            return new HelpFragment();
        } else if (resourceId == R.id.nav_contact_us) {
            return new ContactUsFragment();
        } else if (resourceId == R.id.person_add_note) {
            return new AddNoteFragment();
        }
        return null;
    }

    private static String getFragmentTagForResourceId(Context context, int resourceId) {
        if (resourceId == R.id.nav_home) {
            return context.getString(R.string.nav_home);
        } else if (resourceId == R.id.nav_people) {
            return context.getString(R.string.nav_people);
        } else if (resourceId == R.id.nav_journal) {
            return context.getString(R.string.nav_journal);
        } else if (resourceId == R.id.nav_settings) {
            return context.getString(R.string.nav_settings);
        } else if (resourceId == R.id.nav_help) {
            return context.getString(R.string.nav_help);
        } else if (resourceId == R.id.nav_contact_us) {
            return context.getString(R.string.nav_contact_us);
        } else if (resourceId == R.id.person_add_note) {
            return context.getString(R.string.person_add_note);
        }
        return null;
    }

    public static Integer getNavigationItemIdForFragmentName(Context context, String fragmentName) {
        if (fragmentName.equals(context.getString(R.string.nav_home))) {
            return R.id.nav_home;
        } else if (fragmentName.equals(context.getString(R.string.nav_people))) {
            return R.id.nav_people;
        } else if (fragmentName.equals(context.getString(R.string.nav_journal))) {
            return R.id.nav_journal;
        } else if (fragmentName.equals(context.getString(R.string.nav_settings))) {
            return R.id.nav_settings;
        } else if (fragmentName.equals(context.getString(R.string.nav_help))) {
            return R.id.nav_help;
        } else if (fragmentName.equals(context.getString(R.string.nav_contact_us))) {
            return R.id.nav_contact_us;
        } else if (fragmentName.equals(context.getString(R.string.person_add_note))) {
            return R.id.nav_people;
        }
        return null;
    }

    public static Uri getContactPhotoUri(ContentResolver cr, String contactId, boolean highRes) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        if (highRes) {
            return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        } else {
            return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        }
    }
}
