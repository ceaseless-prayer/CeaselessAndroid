package org.theotech.ceaselessandroid.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;

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
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent mainIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        Notification mNotify = new Builder(this)
                .setContentTitle(getString(R.string.reminder_notification_title))
                .setContentText(getString(R.string.reminder_notification_message))
                .setSmallIcon(R.drawable.ic_notification_dove)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        // TODO should we set 1 as the id for all notifications from this?
        notificationManager.notify(1, mNotify);
        Log.d(TAG, "notification posted.");
        stopSelf();
    }

}
