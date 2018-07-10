package org.theotech.ceaselessandroid.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.fragment.AboutFragment;
import org.theotech.ceaselessandroid.fragment.AddNoteFragment;
import org.theotech.ceaselessandroid.fragment.ContactUsFragment;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.fragment.HelpFragment;
import org.theotech.ceaselessandroid.fragment.HomeFragment;
import org.theotech.ceaselessandroid.fragment.JournalFragment;
import org.theotech.ceaselessandroid.fragment.PeopleFragment;
import org.theotech.ceaselessandroid.fragment.PersonFragment;
import org.theotech.ceaselessandroid.fragment.SettingsFragment;

/**
 * Created by uberx on 10/8/15.
 */
public class FragmentUtils {
    private static final String TAG = FragmentUtils.class.getSimpleName();

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation,
                                    int resourceId) {
        loadFragment(activity, fragmentManager, navigation, resourceId, (Bundle) null);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation,
                                    int resourceId, Bundle loadingFragmentState /* state of the fragment being loaded */) {
        loadFragment(activity, fragmentManager, navigation, resourceId, loadingFragmentState, null);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation,
                                    int resourceId, FragmentState backStackInfo) {
        loadFragment(activity, fragmentManager, navigation, resourceId, null, backStackInfo);
    }

    public static void loadFragment(Activity activity, FragmentManager fragmentManager, NavigationView navigation,
                                    int resourceId, Bundle loadingFragmentState /* state of the fragment being loaded */,
                                    FragmentState backStackInfo) {
        Fragment fragment = getFragmentForResourceId(resourceId);
        String fragmentTag = getFragmentTagForResourceId(activity, resourceId);
        Fragment fragmentForTag = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null && (fragmentForTag == null || !fragmentForTag.isVisible())) {
            MainActivity main = (MainActivity) activity;
            if (backStackInfo != null) {
                main.getFragmentBackStackManager().add(backStackInfo);
            }

            if (resourceId == R.id.nav_home || resourceId == R.id.show_more_people) {
                // if the user tapped Home in the navigation
                // ensure we treat it as if it were the first time the fragment
                // were created (e.g. showing the first page instead of where they left off)
                main.setHomeFragmentCreated(false);
            }

            fragment.setArguments(loadingFragmentState);
            fragmentManager.beginTransaction().replace(R.id.fragment, fragment, fragmentTag).commit();

            if (navigation != null && fragmentTag != null) {
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
        } else if (resourceId == R.id.show_more_people) {
            return new HomeFragment();
        } else if (resourceId == R.id.person_add_note) {
            return new AddNoteFragment();
        } else if (resourceId == R.id.person_card) {
            return new PersonFragment();
        } else if (resourceId == R.id.add_note_fragment) {
            return new AddNoteFragment();
        } else if (resourceId == R.id.nav_about) {
            return new AboutFragment();
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
        } else if (resourceId == R.id.nav_about) {
            return context.getString(R.string.nav_about);
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
        } else if (fragmentName.equals(context.getString(R.string.nav_about))) {
            return R.id.nav_about;
        } else if (fragmentName.equals(context.getString(R.string.person_add_note))) {
            return R.id.nav_journal;
        } else if (fragmentName.equals(context.getString(R.string.person_view))) {
            return R.id.person_card;
        }
        return null;
    }
}
