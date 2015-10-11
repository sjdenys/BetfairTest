package com.example.sjden.betfairtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class SplashScreenActivity extends Activity implements ActivityResponseListener {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    private ProgressBar prgrssbrLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        prgrssbrLogin = (ProgressBar)findViewById(R.id.prgrssbrLogin);;

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                SharedPreferences settings = getSharedPreferences("LoginDataFile", Context.MODE_PRIVATE);
                if(settings.getString("username","f").compareTo("f") == 0) {
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
                else{
                    prgrssbrLogin.setVisibility(View.VISIBLE);
                    LoginHandler lgnhndlr = new LoginHandler();
                    lgnhndlr.setActivityResponseListener(SplashScreenActivity.this);
                    switch(settings.getString("username","f")){
                        case "TestAPI1":
                            APINGRequester.setStrAppKey("QVrWlekPUgCFgGTm");
                            break;
                        case "TestAPI3":
                            APINGRequester.setStrAppKey("VrMZrllcdZbdWvgG");
                            break;
                        case "TestAPI4":
                            APINGRequester.setStrAppKey("EjnCr4rFBdNgESdI");
                            break;
                        case "TestAPI5":
                            APINGRequester.setStrAppKey("JYRDt5abFgNeTFr4");
                            break;
                        case "TestAPI6":
                            APINGRequester.setStrAppKey("QAih9xYca97AKsZL");
                            break;
                    }
                    APINGAccountRequester.setDblAusBalance((double)settings.getLong("balance",0));
                    lgnhndlr.sendLoginRequest(settings.getString("username","f"),settings.getString("password","f"));
                }

            }
        }, SPLASH_TIME_OUT);
    }

    public void responseReceived(String strResponseReceived) {
        if(strResponseReceived.compareTo("SUCCESS") == 0){
            SharedPreferences spSettings = this.getSharedPreferences("LoginDataFile", Context.MODE_PRIVATE);
            SharedPreferences.Editor speEditor = spSettings.edit();
            speEditor.putLong("balance", Double.doubleToRawLongBits(APINGAccountRequester.getDblAusBalance()));
            speEditor.apply();
            Intent intnt = new Intent(this, RaceTypeActivity.class);
            startActivity(intnt);
            finish();
        }
        else{
            Intent intnt = new Intent(this, LoginActivity.class);
            startActivity(intnt);
            finish();
        }
    }

}
