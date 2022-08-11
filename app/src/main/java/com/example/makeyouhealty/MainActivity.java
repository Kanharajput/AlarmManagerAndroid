package com.example.makeyouhealty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    // object for toggle button
    private ToggleButton alarmToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmToggle = findViewById(R.id.alarmToggle);

        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // so here the first parameter is the toggle button that is alarmToggle and
            // isChecked represent the state of toggle button
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String toastMessage = "some faults";
                if(isChecked) {
                    toastMessage = "Stand-up alarm is on";
                } else {
                    toastMessage = "stand-up alarm is off";
                }
                // MainActivity.this is to pass the context of activity otherwise it is passing onCheckedChangeListener
                Toast.makeText(MainActivity.this,toastMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }
}