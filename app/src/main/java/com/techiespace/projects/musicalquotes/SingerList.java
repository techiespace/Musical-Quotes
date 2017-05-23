package com.techiespace.projects.musicalquotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

public class SingerList extends AppCompatActivity {

    CheckBox ch1, ch2, ch3, ch4, ch5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_list);
        ch1 = (CheckBox) findViewById(R.id.checkBox);
        ch2 = (CheckBox) findViewById(R.id.checkBox2);
        ch3 = (CheckBox) findViewById(R.id.checkBox3);
        ch4 = (CheckBox) findViewById(R.id.checkBox4);
        ch5 = (CheckBox) findViewById(R.id.checkBox5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sh1 = getSharedPreferences("MyOwnShared", MODE_APPEND);
        Boolean b1 = sh1.getBoolean("1", true);
        Boolean b2 = sh1.getBoolean("2", true);
        Boolean b3 = sh1.getBoolean("3", true);
        Boolean b4 = sh1.getBoolean("4", true);
        Boolean b5 = sh1.getBoolean("5", true);
        ch1.setChecked(b1);
        ch2.setChecked(b2);
        ch3.setChecked(b3);
        ch4.setChecked(b4);
        ch5.setChecked(b5);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sh = getSharedPreferences("MyOwnShared", MODE_PRIVATE);
        SharedPreferences.Editor myedit = sh.edit();
        myedit.putBoolean("1", ch1.isChecked());
        myedit.putBoolean("2", ch2.isChecked());
        myedit.putBoolean("3", ch3.isChecked());
        myedit.putBoolean("4", ch4.isChecked());
        myedit.putBoolean("5", ch5.isChecked());
        myedit.apply();
    }
}
