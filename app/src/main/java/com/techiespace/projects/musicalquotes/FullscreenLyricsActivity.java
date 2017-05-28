package com.techiespace.projects.musicalquotes;

import android.annotation.SuppressLint;
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
    ImageView img;
    int generate[] = new int[5];    //i don't like declaring some of them here as global and some others
    int lyrici;                     //as local in the code, tried to use java and set a on click listener, but
    int randSinger;                 //the compiler yells saying Variable is accessed within inner class needs to be declared final
    String youtube[][] = new String[30][5];//do i want them as final? for now let's use global...
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
        String lyrics[][] = new String[30][5];
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
        getLyric(lyrics);
    }

    public void getLyric(String[][] lyrics) {
        int cntPerSinger = 0;
        lyrics[0][0] = "Taylor Swift - You Belong With Me";
        lyrics[0][1] = "Taylor Swift - Love Story";
        lyrics[1][0] = "Ed Sheeran - Shape of You";
        lyrics[1][1] = "Ed Sheeran - Castle On The Hill";
        lyrics[2][0] = "DJ Snake - Let Me Love You ft. Justin Bieber";
        lyrics[2][1] = "Justin Bieber - Baby ft. Ludacris";
        lyrics[3][0] = "Passenger | Let Her Go";
        lyrics[3][1] = "Passenger | Anywhere";
        lyrics[4][0] = "Katy Perry - Chained To The Rhythm";
        lyrics[4][1] = "Katy Perry - Firework";

        youtube[0][0] = "VuNIsY6JdUw";
        youtube[0][1] = "8xg3vE8Ie_E";
        youtube[1][0] = "JGwWNGJdvx8";
        youtube[1][1] = "K0ibBPhiaG0";
        youtube[2][0] = "euCqAq6BRa4";
        youtube[2][1] = "kffacxfA7G4";
        youtube[3][0] = "RBumgq5yVrA";
        youtube[3][1] = "cb5PalnCrhY";
        youtube[4][0] = "Um7pMggPnug";
        youtube[4][1] = "QGJuMBdaqIw";

        Random imgSelect = new Random();
        for (int i = 0; i < 5; i++) {    //assume 10 images per singer
            if (lyrics[generate[randSinger] - 1][i] != null)
                cntPerSinger++;
        }
        lyrici = imgSelect.nextInt(cntPerSinger);  //lyrics.length generates a runtime error
        String lyric = new String(lyrics[generate[randSinger] - 1][lyrici]);    //-1 to account for the images starting from 1
        TextView lyricTextView = (TextView) findViewById(R.id.lyricTextView);
        Typeface font = Typeface.createFromAsset(getAssets(), "Pacifico-Regular.ttf");
        lyricTextView.setTypeface(font);
        lyricTextView.setText(lyric);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void openyoutube(View view) {
        if (isNetworkAvailable()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + youtube[generate[randSinger] - 1][lyrici]));
            startActivity(intent);
            FullscreenLyricsActivity.this.finish();
        } else {
            Toast.makeText(this, "No Internet Access", Toast.LENGTH_SHORT).show();
        }
    }


}


