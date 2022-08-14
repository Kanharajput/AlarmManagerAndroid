package com.example.makeyouhealty;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;

    // unique notification id and notification channel string
    private final int NOTIFICATION_ID = 0;
    private final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // get the notification manager
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        deliverNotification(context);
    }

    private void deliverNotification(Context context) {
        createNotificationChannel();
        // when user clicks on notification it opens up the Activity
        Intent contentIntent = new Intent(context,MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context,
                                                                        NOTIFICATION_ID,
                                                                        contentIntent,
                                                                        PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,PRIMARY_CHANNEL_ID);
        notification.setSmallIcon(R.drawable.walk);
        notification.setDefaults(NotificationCompat.DEFAULT_ALL);
        notification.setContentIntent(contentPendingIntent);
        notification.setContentTitle(context.getString(R.string.notification_title));
        notification.setContentText(context.getString(R.string.notification_second_line));
        notification.setAutoCancel(true);

        // deliver the notification
        notificationManager.notify(NOTIFICATION_ID,notification.build());
    }

    private void createNotificationChannel() {
        // notification channel only work above android version 8.1
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                                                                        "stand_up_notification",
                                                                                NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            // this option is not present in my phone
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("notify every 15 minutes to stand and walk");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}