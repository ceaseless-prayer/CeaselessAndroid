package org.theotech.ceaselessandroid.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.util.Constants;

public class ShowNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = ShowNotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // NOTE code is a copy of the DailyNotificationService class which is now deprecated.
        Log.d(TAG, "Creating notification");
        Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_MUTABLE);

        // get the name of the next day's person
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String nextPersonName = sp.getString(Constants.PRESELECTED_PERSON_NAME, null);
        String notificationMessage;

        if (nextPersonName != null) {
            Integer count = Integer.parseInt(sp.getString(Constants.NUMBER_OF_PEOPLE_TO_PRAY_FOR, "3")) - 1;
            Resources res = context.getResources();
            String otherString = res.getQuantityString(R.plurals.number_of_other_people, count, count);
            notificationMessage = context.getString(R.string.reminder_notification_message, nextPersonName, otherString);
        } else {
            notificationMessage = context.getString(R.string.reminder_notification_message_default);
        }

        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, Constants.DEFAULT_CEASELESS_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.ic_notification_dove)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        Notification notification = nb.build();
        Log.d(TAG, "Created notification");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // TODO should we set 1 as the id for all notifications from this?
        notificationManager.notify(1, notification);
        Log.d(TAG, "notification posted.");
    }
}

