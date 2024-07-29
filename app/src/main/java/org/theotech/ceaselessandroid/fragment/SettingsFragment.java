package org.theotech.ceaselessandroid.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.notification.DailyNotificationReceiver;
import org.theotech.ceaselessandroid.prefs.TimePreference;
import org.theotech.ceaselessandroid.prefs.TimePreferenceDialog;
import org.theotech.ceaselessandroid.scripture.ScriptureService;
import org.theotech.ceaselessandroid.scripture.ScriptureServiceImpl;
import org.theotech.ceaselessandroid.util.AnalyticsUtils;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private FragmentStateListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
        // notify fragment state
        try {
            mListener = (FragmentStateListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FragmentStateListener");
        }
        mListener.notify(new FragmentState(getString(R.string.nav_settings)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.nav_settings));

        // HACK since styling preferences is convoluted/messy/broken we apply a style on create
        // and undo what we applied back to the our app's overall theme on destroy.
        // Selectively apply needed styling for preference page.
        // otherwise things like the switch will be invisible because our original accent color is white.
        getActivity().getTheme().applyStyle(R.style.AppTheme_PreferenceScreen, true);

        // create view
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.setBackgroundColor(getResources().getColor(R.color.preferenceBackgroundColor));
        return v;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            dialogFragment = new TimePreferenceDialog();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getActivity().getSupportFragmentManager(),
                    TimePreferenceDialog.class.getName());
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onDestroyView() {
        // HACK see the note in onCreateView about styling preferences.
        // this is where we undo what we applied.
        getActivity().getTheme().applyStyle(R.style.AppTheme_RevertFromPreferenceScreen, true);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        AnalyticsUtils.sendScreenViewHit(this.getActivity(), "SettingsScreen");
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if ("notificationTime".equals(s) || "showNotifications".equals(s)) {
            createOrUpdateTimer();
        }
        if ("preferredBibleVersion".equals(s)){
           ScriptureService ss = ScriptureServiceImpl.getInstance(getActivity());
           ss.clearCache();
           ss.asyncPopulateCache();
           Toast.makeText(getActivity(), getString(R.string.bible_version_will_change), Toast.LENGTH_LONG).show();
        }
    }

    private void createOrUpdateTimer() {
        Intent dailyNotificationReceiver = new Intent(getActivity(), DailyNotificationReceiver.class);
        getActivity().sendBroadcast(dailyNotificationReceiver);
    }
}
