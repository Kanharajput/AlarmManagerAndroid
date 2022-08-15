package com.example.makeyouhealty;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    // object for toggle button
    private ToggleButton alarmToggle;
    private NotificationManager notificationManager;

    // unique notification id and notification channel string
    private final int NOTIFICATION_ID = 0;

    // alarm manager for the system
    AlarmManager alarmManager;

    // objects for the textview
    TextView alarmInfoShowingTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising the alarmInfoShowingTxt
        alarmInfoShowingTxt = findViewById(R.id.showNextAlarmTextview);

        // intialising the alarmToggle data member
        alarmToggle = findViewById(R.id.alarmToggle);
        // intialise the notification manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // intent to send a broadcast
        Intent notifyIntent = new Intent(this,AlarmReceiver.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this,
                                                                                NOTIFICATION_ID,
                                                                                notifyIntent,
                                                                                PendingIntent.FLAG_IMMUTABLE);

        // Initialise the alarm manager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // so here the first parameter is the toggle button that is alarmToggle and
            // isChecked represent the state of toggle button
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String toastMessage = "some faults";
                if(isChecked) {
                    toastMessage = "Stand-up alarm is on";

                    // repeat time for the alarm in milliseconds for 10 seconds
                    // understand it like an alarm without snooze mode for example we set an alarm for 6 am
                    // and then we also set that each morning 6 am. and how this each morning is calculated
                    // 24 hours letter after 6am. so here it is that
                    long repeatTimeInterval = 10*1000;
                    // elapsedRealTime returnt the total time since booted and
                    // also counting when cpu is in saving mode or screen is off
                    // trigger alarm when elapsed time will increases by 1 minute
                    long triggerTime = (long) (SystemClock.elapsedRealtime() + repeatTimeInterval);

                    // this method repeat till we not off the toggle button
                    // ELAPSED_REALTIME_WAKEUP will also wake up the device if it will be sleeping
                    // here repeating time is 10 seconds but it is not pop ups accuratly at that time
                    // might be after 15, 20 , 30 or any no of seconds but not less than 10
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                                    triggerTime,
                                                    repeatTimeInterval,
                                                    notifyPendingIntent);
                } else {
                    toastMessage = "stand-up alarm is off";
                    notificationManager.cancelAll();                  // cancel all the notification belongs to the activity

                    if(alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntent);         // cancel the alarm
                    }
                }
                // MainActivity.this is to pass the context of activity otherwise it is passing onCheckedChangeListener
                Toast.makeText(MainActivity.this,toastMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getNextAlarm(View view) {
        AlarmManager.AlarmClockInfo nextAlarmInfo = alarmManager.getNextAlarmClock();

        // nextAlarmInfo is null if there is no next alarm scheduled
        if(nextAlarmInfo != null) {
            long triggerTimeInMills = nextAlarmInfo.getTriggerTime();
            //alarmInfoShowingTxt.setText(String.valueOf(triggerTimeInMills));

            // below code is use to covert the utc to ist
            // it also include day date time ist
            Date date = new Date(triggerTimeInMills);
            String timeZone = "IST";
            Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
            alarmInfoShowingTxt.setText(local.toString());
        }
    }

    public void setTimeForNotification(View view) {
    }
}