package com.techiespace.projects.musicalquotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
        Intent i1 = new Intent();
        i1.setAction("com.techies.MusicalQuotesScreen");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this, 0, i1, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to start at approximately 6:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour_x);
        calendar.set(Calendar.MINUTE, minute_x);
        if (calendar.before(Calendar.getInstance())) {   //fixed the immediate trigger for past time problem
            calendar.add(Calendar.DATE, 1);
        }
        myAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pd);    //Sets an inexact repeating alarm
        String am_pm;
        int am_or_pm = calendar.get(Calendar.AM_PM);
        if (am_or_pm == 1)
            am_pm = "pm";
        else
            am_pm = "am";
        Toast.makeText(this, "A beautiful quote on it's way on " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " " + am_pm, Toast.LENGTH_SHORT).show();
    }

    public void stopAlarm(View view) {
        Intent i1 = new Intent();
        i1.setAction("com.techies.MusicalQuotesScreen");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this, 0, i1, PendingIntent.FLAG_UPDATE_CURRENT);
        myAlarmManager.cancel(pd);
    }

    public void setAlarm(View view) {
        int cnt = 0;
        SharedPreferences sh = getSharedPreferences("MyOwnShared", MODE_APPEND);
        for (int i = 1; i < 6; i++) {    //5 checkboxes
            if (sh.getBoolean(i + "", false)) {
                cnt++;
            }
        }
        if (cnt == 0)
            Toast.makeText(this, "Select at least one Singer from Singer List", Toast.LENGTH_LONG).show();
        else
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
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putInt("h", hour_x);
        myEdit.putInt("m", minute_x);
        myEdit.apply();     //`commit` writes its data to persistent storage immediately, whereas `apply` will handle it in the background
    }

    public void singerActivity(View view) {
        Intent singerListIntent = new Intent(this, SingerList.class);
        startActivity(singerListIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feedback:
                //send feedback email
                Intent iEmail = new Intent(Intent.ACTION_SEND);
                iEmail.setData(Uri.parse("email"));
                String[] emailAdd = {"shreyas.techiespace@gmail.com"};
                iEmail.putExtra(Intent.EXTRA_EMAIL, emailAdd);
                iEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Musical Quotes");
                iEmail.setType("message/rfc822");   //what does this mean?
                Intent iLaunchEmail = Intent.createChooser(iEmail, "Launch Email");
                startActivity(iLaunchEmail);
                return true;
            case R.id.suggest:
                //send suggest MusiQuote email
                Intent iSuggest = new Intent(Intent.ACTION_SEND);
                iSuggest.setData(Uri.parse("email"));
                String[] suggestAdd = {"shreyas.techiespace@gmail.com"};
                iSuggest.putExtra(Intent.EXTRA_EMAIL, suggestAdd);
                iSuggest.putExtra(Intent.EXTRA_SUBJECT, "Suggestion to add to a Musical Quote");
                iSuggest.setType("message/rfc822");   //what does this mean?
                Intent iSuggestEmail = Intent.createChooser(iSuggest, "Launch Email");
                startActivity(iSuggestEmail);
                return true;
            case R.id.gitHub:
                //Open Musical Quotes GitHub repo
                Intent iGitHub = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/techiespace/Musical-Quotes"));
                startActivity(iGitHub);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
