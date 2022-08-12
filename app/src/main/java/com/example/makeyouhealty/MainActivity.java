package com.example.makeyouhealty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    // object for toggle button
    private ToggleButton alarmToggle;
    private NotificationManager notificationManager;

    // unique notification id and notification channel string
    private final int NOTIFICATION_ID = 0;
    private final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // intialising the alarmToggle data member
        alarmToggle = findViewById(R.id.alarmToggle);

        // get the notification manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // create the notification channel
        createNotificationChannel();

        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // so here the first parameter is the toggle button that is alarmToggle and
            // isChecked represent the state of toggle button
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String toastMessage = "some faults";
                if(isChecked) {
                    toastMessage = "Stand-up alarm is on";
                    deliverNotification(MainActivity.this);
                } else {
                    toastMessage = "stand-up alarm is off";
                    notificationManager.cancelAll();                  // cancel all the notification belongs to the activity
                }
                // MainActivity.this is to pass the context of activity otherwise it is passing onCheckedChangeListener
                Toast.makeText(MainActivity.this,toastMessage,Toast.LENGTH_SHORT).show();
            }
        });
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

    // to understand about this more check github we already did it
    private void deliverNotification(Context context) {
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
        notification.setContentTitle(getString(R.string.notification_title));
        notification.setContentText(getString(R.string.notification_second_line));
        notification.setAutoCancel(true);

        // deliver the notification
        notificationManager.notify(NOTIFICATION_ID,notification.build());
    }

}