package com.techiespace.projects.musicalquotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AlarmManager myAlarmManager;
    int hour_x = 0;
    int minute_x = 0;
    protected TimePickerDialog.OnTimeSetListener kTimePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    hour_x = hourOfDay;
                    minute_x = minute;
                    Toast.makeText(MainActivity.this, hour_x + " : " + minute_x, Toast.LENGTH_SHORT).show();
                    //fire the alarm after the user sets.
                    startAlarm(hour_x, minute_x);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    public void startAlarm(int hour_x, int minute_x) {
        Toast.makeText(this, "Alarm Started", Toast.LENGTH_SHORT).show();
        Intent i1 = new Intent();
        i1.setAction("com.techies.MusicalQuotesScreen");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this, 0, i1, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to start at approximately 6:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour_x);
        calendar.set(Calendar.MINUTE, minute_x);
        myAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pd);
    }

    public void stopAlarm(View view) {
        Intent i1 = new Intent();
        i1.setAction("com.techies.MusicalQuotesScreen");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this, 0, i1, PendingIntent.FLAG_UPDATE_CURRENT);
        myAlarmManager.cancel(pd);
        Toast.makeText(this, "Alarm Stopped", Toast.LENGTH_SHORT).show();
    }

    public void setAlarm(View view) {
        new TimePickerDialog(MainActivity.this, kTimePickerListener, hour_x, minute_x, false).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sh1 = getSharedPreferences("MyOwnShared", MODE_APPEND);
        hour_x = sh1.getInt("h", 0);
        minute_x = sh1.getInt("m", 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sh = getSharedPreferences("MyOwnShared", MODE_PRIVATE);
        SharedPreferences.Editor myedit = sh.edit();
        myedit.putInt("h", hour_x);
        myedit.putInt("m", minute_x);
        myedit.commit();
    }
}
