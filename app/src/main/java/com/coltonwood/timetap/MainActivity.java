package com.coltonwood.timetap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    long timeElapsed, seconds = 0, millis = 0, tapTimeElapsed = 0;
    long startTimeinMilli, currentTimeinMilli, tapTime;
    TextView restartTextTV;
    TextView lastscoreTV, highscoreReadyTV, currentScoreTV, highscoreTV, tapDifferenceTV, timerTV;

    RelativeLayout readyRL, playingRL, gameoverRL;
    int gamestate = 0;
    GameState gameState;

    String LOGCAT = "TAPTIME_LOG";
    String MY_PREFS_NAME = "timetapprefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // textviews
        lastscoreTV = (TextView)findViewById(R.id.lastscore_text);
        highscoreReadyTV = (TextView)findViewById(R.id.highscore_text_ready);
        highscoreTV = (TextView)findViewById(R.id.highscore_text_gameover);
        currentScoreTV = (TextView)findViewById(R.id.current_score);
        tapDifferenceTV = (TextView)findViewById(R.id.tap_counter);
        timerTV = (TextView)findViewById(R.id.timer);

        // gamestate screens
        readyRL = (RelativeLayout)findViewById(R.id.ready_screen);
        playingRL = (RelativeLayout)findViewById(R.id.playing_screen);
        gameoverRL = (RelativeLayout)findViewById(R.id.gameover_screen);
        readyRL.setVisibility(View.VISIBLE);

        gameState = new GameState();


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int highscore = prefs.getInt("highscore", 0);
        int lastscore = prefs.getInt("lastscore", 0);

        highscoreReadyTV.setText("highscore: " + highscore);
        lastscoreTV.setText("last score: " + lastscore);


    }

    public void handleTap(View view) {

        tapTime = System.currentTimeMillis();

        switch (gamestate) {

            // READY
            case 0:
                Log.i(LOGCAT, "ready tap");
                startTimer();
                break;

            // PLAYING
            case 1:
                Log.i(LOGCAT, "playing tap");
                // show tap offset
                if (millis < 200 || millis > 800) {
                    tapDifferenceTV.setText(millis > 500 ? "-" + (1000 - millis) : "+" + millis);
                }
                // you lose
                else {
                    Log.i(LOGCAT, "gameover tap");
                    gameover();
                }
                break;

            // GAMEOVER
            case 2:
                Log.i(LOGCAT, "gameover tap");
                View myView = gameoverRL;
                circleHide(myView);
                resetGame();
                break;

            // idk yet?
            default:
                break;
        }

    }

    public void startTimer() {

        gamestate = GameState.PLAYING;
        playingRL.setVisibility(View.VISIBLE);
        gameoverRL.setVisibility(View.INVISIBLE);
        readyRL.setVisibility(View.INVISIBLE);

        Log.i(LOGCAT, String.valueOf("PLAYING"));

        startTimeinMilli = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        tapTime = System.currentTimeMillis();
        tapDifferenceTV.setText("+0");

        // fade out timer textview over 12 seconds
//        timerTV.animate().setDuration(12000).alpha(0);

    }

    public void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    // READY
    public void resetGame() {

        gamestate = gameState.READY;
        playingRL.setVisibility(View.INVISIBLE);
        readyRL.setVisibility(View.VISIBLE);

        Log.i(LOGCAT, String.valueOf("READY"));

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int highscore = prefs.getInt("highscore", 0);
        int lastscore = prefs.getInt("lastscore", 0);

        highscoreReadyTV.setText("highscore: " + highscore);
        lastscoreTV.setText("last score: " + lastscore);

        // reset values for gameplay
        millis = 0;
        seconds = 0;
    }

    // GAMEOVER
    public void gameover() {

        stopTimer();
        gamestate = gameState.GAMEOVER;
        playingRL.setVisibility(View.INVISIBLE);
        readyRL.setVisibility(View.INVISIBLE);

        View myView = gameoverRL;
        circleReveal(myView);

        Log.i(LOGCAT, String.valueOf("GAMEOVER"));

//        currentScoreTV.setText( seconds == 0 ? Long.toString(millis) : Long.toString(seconds) + Long.toString(millis));

        // calculate current score
        int currentscore = (int) (seconds*1000 + millis);
        currentScoreTV.setText(Integer.toString(currentscore));

        // preferences
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        // get old highscore
        int oldhighscore = prefs.getInt("highscore", 0);

        // NEW HIGHSCORE!
        if (currentscore > oldhighscore) {
            editor.putInt("highscore", (int)(seconds*1000 + millis));
            highscoreTV.setText("New highscore!");
        }
        else {
            highscoreTV.setText(" ");
        }

        // store current score as last score
        editor.putInt("lastscore", currentscore);
        editor.apply();

    }

    // timer logic
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            // update timer
            currentTimeinMilli = System.currentTimeMillis();
            timeElapsed = currentTimeinMilli - startTimeinMilli;
            tapTimeElapsed = currentTimeinMilli - tapTime;

            if (seconds == 0) {
                timerTV.setAlpha(1);
            }
            else if (seconds == 1) {
                timerTV.setAlpha((float) 0.9);
            }
            else if (seconds == 2) {
                timerTV.setAlpha((float) 0.9);
            }
            else if (seconds == 3) {
                timerTV.setAlpha((float) 0.8);
            }
            else if (seconds == 4) {
                timerTV.setAlpha((float) 0.7);
            }
            else if (seconds == 5) {
                timerTV.setAlpha((float) 0.6);
            }
            else if (seconds == 6) {
                timerTV.setAlpha((float) 0.5);
            }
            else if (seconds == 7) {
                timerTV.setAlpha((float) 0.4);
            }
            else if (seconds == 8) {
                timerTV.setAlpha((float) 0.3);
            }
            else if (seconds == 9) {
                timerTV.setAlpha((float) 0.2);
            }
            else if (seconds == 10) {
                timerTV.setAlpha((float) 0.1);
            }
            else if (seconds == 11) {
                timerTV.setAlpha((float) 0.0);
            }

            // check for gameover
            if (tapTimeElapsed > 1400) {
                gameover();

            } else {

                // update time values
                millis = timeElapsed;
                if (timeElapsed > 999) {
                    seconds++;
                    millis = 999;
                    startTimeinMilli = currentTimeinMilli;
                }

                // convert time to strings
                String secondsStr, millisStr;
                NumberFormat f = new DecimalFormat("000");
                secondsStr = Long.toString(seconds);
                millisStr = f.format(millis);

                // set timer text
                timerTV.setText(secondsStr + "." + millisStr);

                timerHandler.postDelayed(this, 10);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        resetGame();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void circleReveal(final View myView) {
        int cx = (int) (myView.getX() + myView.getWidth() / 2);
        int cy = (int) (myView.getY() + myView.getHeight() / 2);

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);

        anim.start();
    }

    public void circleHide(final View myView) {
        int cx = (int) (myView.getX() + myView.getWidth() / 2);
        int cy = (int) (myView.getY() + myView.getHeight() / 2);

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }

        });

        // start the animation
        anim.start();
    }
}
