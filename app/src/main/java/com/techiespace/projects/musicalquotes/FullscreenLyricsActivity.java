package com.techiespace.projects.musicalquotes;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenLyricsActivity extends AppCompatActivity {

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    ImageView img;
    int generate[] = new int[5];    //i don't like declaring some of them here as global and some others
    int lyrici;                     //as local in the code, tried to use java and set a on click listener, but
    int randSinger;                 //the compiler yells saying Variable is accessed within inner class needs to be declared final
    lyricsc lyricObj[][] = new lyricsc[30][5];
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int cnt = 0;
        SharedPreferences sh = getSharedPreferences("MyOwnShared", MODE_APPEND);
        for (int i = 1; i < 6; i++) {    //5 checkboxes
            if (sh.getBoolean(i + "", false)) {
                cnt++;
            }
        }
        if (cnt == 0) {
            Toast.makeText(this, "Select at least one Singer from Singer List", Toast.LENGTH_LONG).show();//toast not working
            System.exit(0);
        }
        setContentView(R.layout.activity_fullscreen_lyrics);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {    //replace this by swipe to distroy activity
            @Override
            public void onClick(View view) {
                //toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.youtube_btn).setOnTouchListener(mDelayHideTouchListener);
        getImage();
        img.setOnTouchListener(new OnSwipeTouchListener(FullscreenLyricsActivity.this) {
            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                System.exit(0);
            }

            public void onSwipeLeft() {
                System.exit(0);
            }

            public void onSwipeBottom() {
            }

        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void getImage() {
        int cnt = 0;
        SharedPreferences sh = getSharedPreferences("MyOwnShared", MODE_APPEND);
        for (int i = 1, j = 0; i < 6; i++) {    //5 checkboxes
            if (sh.getBoolean(i + "", true)) {
                generate[j] = i;
                j++;
                cnt = j;
            }
        }
        //generate a random number
        Random imgselect = new Random();
        randSinger = imgselect.nextInt(cnt);
        int randimg = imgselect.nextInt(2) + 1;
        img = (ImageView) findViewById(R.id.fullscreen_content);
        String imgName = "i" + generate[randSinger] + "_" + randimg;
        img.setImageResource(getResources().getIdentifier(imgName, "drawable", getPackageName()));
        getLyricDetails();

        //cancel and reset alarm
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);

        Intent i1 = new Intent();
        i1.setAction("com.techies.MusicalQuotesScreen");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this, 0, i1, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences sh1 = getSharedPreferences("MyOwnShared", MODE_APPEND);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putInt("toggleMin", 2);
        int hour_x = sh1.getInt("h", 0);
        int minute_x = sh1.getInt("m", 0);

        // Set the alarm to start at approximately 6:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour_x);
        calendar.set(Calendar.MINUTE, minute_x);
        if (calendar.before(Calendar.getInstance())) {   //fixed the immediate trigger for past time problem
            calendar.add(Calendar.DATE, 1);
        }
        AlarmManager myAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        myAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pd);    //Sets an inexact repeating alarm
        Toast.makeText(this, "A beautiful quote on it's way on " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " " + calendar.get(Calendar.AM_PM), Toast.LENGTH_SHORT).show();

    }

    public void getLyricDetails() {
        int cntPerSinger = 0;

        lyricObj[0][0] = new lyricsc();
        lyricObj[0][0].setLyric("Wondering if I dodged a bullet or just lost the love of my life");
        lyricObj[0][0].setSong("I don't wanna live forever");
        lyricObj[0][0].setSinger("~Taylor Swift~");
        lyricObj[0][0].setYoutube("7F37r50VUTQ");

        lyricObj[0][1] = new lyricsc();
        lyricObj[0][1].setLyric("Words... How little they mean, when you're a little too late");
        lyricObj[0][1].setSong("Sad Beautiful Tragic");
        lyricObj[0][1].setSinger("~Taylor Swift~");
        lyricObj[0][1].setYoutube("zmhOVonuOcU");

        lyricObj[1][0] = new lyricsc();
        lyricObj[1][0].setLyric("It's ok to be not ok.");
        lyricObj[1][0].setSong("Who You Are");
        lyricObj[1][0].setSinger("~Ed Sheeran~");
        lyricObj[1][0].setYoutube("BjiEaSInikE");

        lyricObj[1][1] = new lyricsc();
        lyricObj[1][1].setLyric("Your love was handmade for somebody like me");
        lyricObj[1][1].setSong("Shape of You");
        lyricObj[1][1].setSinger("~Ed Sheeran~");
        lyricObj[1][1].setYoutube("_dK2tDK9grQ");


        lyricObj[2][0] = new lyricsc();
        lyricObj[2][0].setLyric("Never say never");
        lyricObj[2][0].setSong("Never say never");
        lyricObj[2][0].setSinger("~Justin Bieber~");
        lyricObj[2][0].setYoutube("ZxkcBxHwFuE");

        lyricObj[2][1] = new lyricsc();
        lyricObj[2][1].setLyric("Oh baby, I know lovin’ you ain’t easy\n" +
                "But it sure is worth a try");
        lyricObj[2][1].setSong("Die in Your Arms");
        lyricObj[2][1].setSinger("~Justin Bieber~");
        lyricObj[2][1].setYoutube("YuHQn4BrkL8");

        lyricObj[3][0] = new lyricsc();
        lyricObj[3][0].setLyric("Or through every emotion\n" +
                "When you know that they don't care\n" +
                "Darling, that's when I'm with you\n" +
                "Oh, I'll go with you anywhere");
        lyricObj[3][0].setSong("Anywhere");
        lyricObj[3][0].setSinger("~Passenger~");
        lyricObj[3][0].setYoutube("cb5PalnCrhY");

        lyricObj[3][1] = new lyricsc();
        lyricObj[3][1].setLyric("Only hate the road when you're missing home\n" +
                "Only know you love her when you let her go");
        lyricObj[3][1].setSong("Let Her Go");
        lyricObj[3][1].setSinger("~Passenger~");
        lyricObj[3][1].setYoutube("Ginx7WKq5GE");

        lyricObj[4][0] = new lyricsc();
        lyricObj[4][0].setLyric("I went from zero to my own hero");
        lyricObj[4][0].setSong("Roar");
        lyricObj[4][0].setSinger("~Katy Perry~");
        lyricObj[4][0].setYoutube("CevxZvSJLk8");

        lyricObj[4][1] = new lyricsc();
        lyricObj[4][1].setLyric("'Cause baby you're a firework\n" +
                "Come on show 'em what your worth\n" +
                "Make 'em go \"Oh, oh, oh!\"\n" +
                "As you shoot across the sky-y-y");
        lyricObj[4][1].setSong("Firework");
        lyricObj[4][1].setSinger("~Katy Perry~");
        lyricObj[4][1].setYoutube("QGJuMBdaqIw");

        Random imgSelect = new Random();
        for (int i = 0; i < 5; i++) {    //assume 10 images per singer
            if (lyricObj[generate[randSinger] - 1][i] != null)
                cntPerSinger++;
        }
        lyrici = imgSelect.nextInt(cntPerSinger);  //lyrics.length generates a runtime error
        String lyric = new String(lyricObj[generate[randSinger] - 1][lyrici].getLyric());    //-1 to account for the images starting from 1
        String song = new String(lyricObj[generate[randSinger] - 1][lyrici].getSong());
        String singer = new String(lyricObj[generate[randSinger] - 1][lyrici].getSinger());
        TextView lyricTextView = (TextView) findViewById(R.id.lyricTextView);
        TextView songTextView = (TextView) findViewById(R.id.songTextView);
        TextView singerTextView = (TextView) findViewById(R.id.singerTextView);


        Typeface font = Typeface.createFromAsset(getAssets(), "Pacifico-Regular.ttf");
        lyricTextView.setTypeface(font);
        lyricTextView.setText(lyric);
        Typeface singerSongfont = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
        singerTextView.setTypeface(singerSongfont);
        songTextView.setText(song);
        songTextView.setTypeface(singerSongfont);
        singerTextView.setText(singer);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void openYoutube(View view) {
        if (isNetworkAvailable()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + lyricObj[generate[randSinger] - 1][lyrici].getYoutube()));
            startActivity(intent);
            FullscreenLyricsActivity.this.finish();
        } else {
            Toast.makeText(this, "No Internet Access", Toast.LENGTH_SHORT).show();
        }
    }

    //String youtube[][] = new String[30][5];//do i want them as final? for now let's use global...
    private class lyricsc {
        String lyric;
        String youtube;
        String singer;
        String song;

        private String getSinger() {
            return singer;
        }

        private void setSinger(String singer) {
            this.singer = singer;
        }

        private String getYoutube() {
            return youtube;
        }

        private void setYoutube(String youtube) {
            this.youtube = youtube;
        }

        private String getLyric() {
            return lyric;
        }

        private void setLyric(String lyric) {
            this.lyric = lyric;
        }

        private String getSong() {
            return song;
        }

        private void setSong(String song) {
            this.song = song;
        }
    }
}