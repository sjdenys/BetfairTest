package com.example.sjden.betfairtest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SplashScreenActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                SharedPreferences settings = getSharedPreferences("LoginDataFile", 0);
                if(settings.getString("username","f").compareTo("f") == 0) {
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

}
