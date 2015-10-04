package org.theotech.ceaselessandroid.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat.Builder;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;

/**
 * Created by jprobert on 10/3/2015.
 */
public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        Notification mNotify = new Builder(this)
                .setContentTitle("Prayer reminder")
                .setContentText("Pray for your friends today.")
                .setSmallIcon(R.drawable.ic_notification_dove)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        mNM.notify(1, mNotify);
    }
}
