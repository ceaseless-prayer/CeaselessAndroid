package org.theotech.ceaselessandroid.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.util.Constants;

/**
 * Created by jprobert on 10/3/2015.
 * This class is responsible for showing notifications to the user at the time they specify.
 * It is called by an alarm that is installed in {@link DailyNotificationReceiver}.
 */
public class DailyNotificationService extends Service {

    private static final String TAG = DailyNotificationService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Creating notification");
        Intent mainIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        // get the name of the next day's person
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String nextPersonName = sp.getString(Constants.PRESELECTED_PERSON_NAME, null);
        String notificationMessage;

        if (nextPersonName != null) {
            Integer count = Integer.parseInt(sp.getString(Constants.NUMBER_OF_PEOPLE_TO_PRAY_FOR, "3")) - 1;
            Resources res = getResources();
            String otherString = res.getQuantityString(R.plurals.number_of_other_people, count, count);
            notificationMessage = getString(R.string.reminder_notification_message, nextPersonName, otherString);
        } else {
            notificationMessage = getString(R.string.reminder_notification_message_default);
        }


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, Constants.DEFAULT_CEASELESS_CHANNEL_ID)
                .setContentTitle(getString(R.string.reminder_notification_title))
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.ic_notification_dove)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        Log.d(TAG, "Created notification");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // TODO should we set 1 as the id for all notifications from this?
        notificationManager.notify(1, notification.build());
        Log.d(TAG, "notification posted.");
        stopSelf();
    }

}
