package org.theotech.ceaselessandroid.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.theotech.ceaselessandroid.prefs.TimePickerDialogPreference;

import java.util.Calendar;

/**
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
        boolean showNotifications = preferences.getBoolean("showNotifications", false);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, DailyNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // always clear existing repeating alarm before we set a new one.
        alarmManager.cancel(pendingIntent);

        if (showNotifications) {
            String notificationTime = preferences.getString("notificationTime", "08:30");
            Calendar firingCal = getNotificationFiringCalendar(notificationTime);

            Log.d(TAG, "Setting daily notification alarm for time: " + notificationTime);

            long intendedTime = firingCal.getTimeInMillis();
            Calendar currentCal = Calendar.getInstance();
            long currentTime = currentCal.getTimeInMillis();

            if (intendedTime >= currentTime) {
                // you can add buffer time too here to ignore some small differences in milliseconds
                Log.d(TAG, "setting alarm for same day.");
                setNotificationAlarm(pendingIntent, alarmManager, intendedTime);

            } else {
                // set from next day
                // you might consider using calendar.add() for adding one day to the current day
                Log.d(TAG, "Setting alarm for next day");
                firingCal.add(Calendar.DAY_OF_MONTH, 1);
                intendedTime = firingCal.getTimeInMillis();
                setNotificationAlarm(pendingIntent, alarmManager, intendedTime);
            }
        } else {
            Log.d(TAG, "No repeating alarm set since notifications are off.");
        }
    }

    private void setNotificationAlarm(PendingIntent pendingIntent, AlarmManager alarmManager, long intendedTime) {

        Log.d(TAG, "Setting intended time to " + intendedTime);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                intendedTime, AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    @NonNull
    private Calendar getNotificationFiringCalendar(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, TimePickerDialogPreference.getMinute(time));
        calendar.set(Calendar.HOUR_OF_DAY, TimePickerDialogPreference.getHour(time));
//        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
        return calendar;
    }
}
