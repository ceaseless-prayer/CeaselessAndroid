package org.theotech.ceaselessandroid.util;

import android.app.Fragment;
import android.content.Context;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.ContactUsFragment;
import org.theotech.ceaselessandroid.fragment.HelpFragment;
import org.theotech.ceaselessandroid.fragment.MainFragment;
import org.theotech.ceaselessandroid.fragment.PeopleFragment;
import org.theotech.ceaselessandroid.fragment.SettingsFragment;
import org.theotech.ceaselessandroid.fragment.VerseFragment;

/**
 * Created by uberx on 10/8/15.
 */
public class ActivityUtils {
    public static Fragment getFragmentForNavigationItemId(int itemId) {
        if (itemId == R.id.nav_home) {
            return new MainFragment();
        } else if (itemId == R.id.nav_people) {
            return new PeopleFragment();
        } else if (itemId == R.id.nav_verse) {
            return new VerseFragment();
        } else if (itemId == R.id.nav_settings) {
            return new SettingsFragment();
        } else if (itemId == R.id.nav_help) {
            return new HelpFragment();
        } else if (itemId == R.id.nav_contact_us) {
            return new ContactUsFragment();
        }
        return null;
    }

    public static String getTitleForNavigationItemId(Context context, int itemId) {
        if (itemId == R.id.nav_home) {
            return context.getString(R.string.app_name);
        } else if (itemId == R.id.nav_people) {
            return context.getString(R.string.nav_people);
        } else if (itemId == R.id.nav_verse) {
            return context.getString(R.string.nav_verse);
        } else if (itemId == R.id.nav_settings) {
            return context.getString(R.string.nav_settings);
        } else if (itemId == R.id.nav_help) {
            return context.getString(R.string.nav_help);
        } else if (itemId == R.id.nav_contact_us) {
            return context.getString(R.string.nav_contact_us);
        }
        return null;
    }

    public static Integer getNavigationItemResourceIdForFragmentName(Context context, String fragmentName) {
        if (fragmentName.equals(context.getString(R.string.app_name))) {
            return R.id.nav_home;
        } else if (fragmentName.equals(context.getString(R.string.nav_people))) {
            return R.id.nav_people;
        } else if (fragmentName.equals(context.getString(R.string.nav_verse))) {
            return R.id.nav_verse;
        } else if (fragmentName.equals(context.getString(R.string.nav_settings))) {
            return R.id.nav_settings;
        } else if (fragmentName.equals(context.getString(R.string.nav_help))) {
            return R.id.nav_help;
        } else if (fragmentName.equals(context.getString(R.string.nav_contact_us))) {
            return R.id.nav_contact_us;
        }
        return null;
    }
}
