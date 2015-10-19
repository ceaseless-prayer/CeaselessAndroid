package org.theotech.ceaselessandroid.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.util.Log;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.fragment.AddNoteFragment;
import org.theotech.ceaselessandroid.fragment.ContactUsFragment;
import org.theotech.ceaselessandroid.fragment.FragmentState;
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

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation,
                                    int resourceId) {
        loadFragment(activity, fragmentManager, navigation, resourceId, (Bundle) null);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation,
                                    int resourceId, Bundle fragmentBundle) {
        loadFragment(activity, fragmentManager, navigation, resourceId, fragmentBundle, null);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation,
                                    int resourceId, FragmentState backStackInfo) {
        loadFragment(activity, fragmentManager, navigation, resourceId, null, backStackInfo);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation,
                                    int resourceId, Bundle fragmentBundle, FragmentState backStackInfo) {
        if (backStackInfo != null) {
            ((MainActivity) activity).getFragmentBackStackManager().add(backStackInfo);
        }
        Fragment fragment = getFragmentForResourceId(resourceId);
        String fragmentTag = getFragmentTagForResourceId(activity, resourceId);
        Fragment fragmentForTag = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null && (fragmentForTag == null || !fragmentForTag.isVisible())) {
            fragment.setArguments(fragmentBundle);
            fragmentManager.beginTransaction().replace(R.id.fragment, fragment, fragmentTag).commit();
            if (navigation != null) {
                navigation.setCheckedItem(getNavigationItemIdForFragmentName(activity, fragmentTag));
            }
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
        } else if (resourceId == R.id.nav_rate_this_app) {
            return new HomeFragment();
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
        } else if (resourceId == R.id.nav_rate_this_app) {
            return context.getString(R.string.nav_rate_this_app);
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
        } else if (fragmentName.equals(context.getString(R.string.nav_rate_this_app))) {
            return R.id.nav_rate_this_app;
        } else if (fragmentName.equals(context.getString(R.string.person_add_note))) {
            return R.id.nav_journal;
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
