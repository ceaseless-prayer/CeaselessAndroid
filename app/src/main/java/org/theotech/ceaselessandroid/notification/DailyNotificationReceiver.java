package org.theotech.ceaselessandroid.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.util.Log;

import org.theotech.ceaselessandroid.prefs.TimePreference;

import java.util.Calendar;

/**
 * This class is invoked when the notification alarm is initialized.
 *
 * Created by kirisu on 10/14/15.
 * This receiver gets broadcasts indicating that the daily notification
 * needs to be changed. It clears out the alarm that sets off the daily notification.
 * It then installs a new alarm as needed. At least three sources broadcast to this receiver.
 * 1. when the application starts {@link org.theotech.ceaselessandroid.activity.MainActivity}.
 * 2. when the phone boots (alarms are cleared and need to be reinstalled on boot).
 * 3. when the notification setting is changed
 */
public class DailyNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = DailyNotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showNotifications = preferences.getBoolean("showNotifications", true);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Using a broadcast receiver to show notification");
            Intent notificationIntent = new Intent(context, ShowNotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            // TODO cleanup this code path.
            // We should be using getBroadcast instead of kicking off a service to show notifications anyway.
            // However, we keep this here to ensure notifications on older versions of the app continue to
            // function. It's tricky to test
            Intent notificationIntent = new Intent(context, DailyNotificationService.class);
            pendingIntent = PendingIntent.getService(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }
        // always clear existing alarm before we set a new one.
        alarmManager.cancel(pendingIntent);

        if (showNotifications) {
            String notificationTime = preferences.getString("notificationTime", "08:00");
            Calendar firingCal = getNotificationFiringCalendar(notificationTime, intent);

            Log.d(TAG, "Attempting to set daily notification alarm for time: " + notificationTime);
            long intendedTime = firingCal.getTimeInMillis();
            setNotificationAlarm(pendingIntent, alarmManager, intendedTime);
            Log.d(TAG, "Setting daily notification alarm for time: " + intendedTime);
        } else {
            Log.d(TAG, "No repeating alarm set since notifications are off.");
        }
    }

    private void setNotificationAlarm(PendingIntent pendingIntent, AlarmManager alarmManager, long intendedTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "Setting exact intended time to " + intendedTime);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, intendedTime, pendingIntent);
            } else {
                Log.d(TAG, "Setting inexact intended time to " + intendedTime);
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, intendedTime, pendingIntent);
            }
        } else {
            // Apps targeting lower sdk versions, can always schedule exact alarms.
            Log.d(TAG, "Older SDK version detected. Setting exact intended time to " + intendedTime);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, intendedTime, pendingIntent);
        }
    }

    @NonNull
    private Calendar getNotificationFiringCalendar(String time, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, TimePreference.parseMinute(time));
        calendar.set(Calendar.HOUR_OF_DAY, TimePreference.parseHour(time)); // Comment out this line for hourly testing
        if (intent.getBooleanExtra("tomorrow", false)) {
            //calendar.add(Calendar.HOUR_OF_DAY, 1); // Uncomment this line for hourly testing
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Comment out this line for hourly testing
        }
        return calendar;
    }
}
