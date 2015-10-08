package org.theotech.ceaselessandroid.fragment;


import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.notification.NotificationService;
import org.theotech.ceaselessandroid.prefs.TimePickerDialogPreference;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getString(R.string.nav_settings));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if ("notificationTime".equals(s)) {
            if (sharedPreferences.getBoolean("showNotifications", true)) {
                createOrUpdateTimer(sharedPreferences);
            }
        }

        if ("showNotifications".equals(s)) {
            if (sharedPreferences.getBoolean("showNotifications", true)) {
                createOrUpdateTimer(sharedPreferences);
            } else {

                cancelTimer();
            }
        }
    }

    private void cancelTimer() {
        Context context = getActivity();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(getActivity(), NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(TAG, "Canceling reminder notification alarm");
        alarmManager.cancel(pendingIntent);
    }

    private void createOrUpdateTimer(SharedPreferences sharedPreferences) {
        Context context = getActivity();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(getActivity(), NotificationService.class);
        String time = sharedPreferences.getString("notificationTime", "08:30");
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, TimePickerDialogPreference.getMinute(time));
        calendar.set(Calendar.HOUR, TimePickerDialogPreference.getHour(time));
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        Log.d(TAG, "Setting reminder notification alarm for time (starting the next day): " + time);
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
