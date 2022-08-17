package com.example.makeyouhealty;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    private EditText userManualTimeForNotificationEdt;


    // unique notification id and notification channel string
    private final int NOTIFICATION_ID = 0;

    // alarm manager for the system
    AlarmManager alarmManager;

    // objects for the textview
    TextView alarmInfoShowingTxt;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising the alarmInfoShowingTxt
        alarmInfoShowingTxt = findViewById(R.id.showNextAlarmTextview);

        // initialising the userManualTimeForNotificationEdt to get the time when we have to send the notification
        userManualTimeForNotificationEdt = findViewById(R.id.getFixTimeToSendNotificationEdt);

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

        // we can't run the setExact without accessing permission to use it in manifest
        // as it trigger at exact time and not let the system to save battery
        // this method will return true if permission granted
        if(alarmManager.canScheduleExactAlarms()) {
            Log.d("SCHEDULE_ALARM","YES");
        }

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
        // the time set by the user to get a notification
        String timeWithHMS = userManualTimeForNotificationEdt.getText().toString();

        if(timeWithHMS == null) {
            Toast.makeText(this,"Please pass a time string",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Notification time is set",Toast.LENGTH_SHORT).show();
            // split the string from where there is a :
            String[] timeValues = timeWithHMS.split(":");
            // getting the current time in ist form as well as in milliseconds

            // getting hour, minute and second differently
            Integer user_hour = Integer.parseInt(timeValues[0]);
            Integer user_minute = Integer.parseInt(timeValues[1]);
            Integer user_second = Integer.parseInt(timeValues[2]);

            // getting the current time
            Date currentTime = Calendar.getInstance().getTime();
            int hours = currentTime.getHours();
            int minutes = currentTime.getMinutes();
            int seconds = currentTime.getSeconds();

            // find out the remaining time to trigger the notification
            // this formula only works when user time is greater than current time
            int hour_remains = user_hour - hours;
            int minutes_remains = user_minute - minutes;
            int seconds_remains = user_second - seconds;

            // convert this remaining time to milliseconds
            long remaining_time_mills = ((((hour_remains * 60) + minutes_remains) * 60) + seconds_remains) * 1000;
            // get the current time in milliseconds
            long current_time_mills = SystemClock.elapsedRealtime();
            // now add the remaining_time_mills to current _time_mills
            // to find out the trigger time in mills
            long trigger_time_mills = current_time_mills + remaining_time_mills;

            // for sending the broadcast
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                                                                            NOTIFICATION_ID,
                                                                            intent,
                                                                            PendingIntent.FLAG_IMMUTABLE);

            // this first parameter of the AlarmManager will tell the method from which clock
            // we are referencing like here ELAPSED_REALTIME_WAKEUP so to get current time in millis we use the same clock
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger_time_mills, pendingIntent);
        }
    }
}