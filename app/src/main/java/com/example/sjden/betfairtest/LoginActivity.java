package com.example.sjden.betfairtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class LoginActivity extends Activity implements ActivityResponseListener {

    private LoginHandler lgnhndlr = new LoginHandler();
    RelativeLayout rl;
    private Button bttnLogIn;
    private EditText edttxtUsername;
    private EditText edttxtPassword;
    private CheckBox chckbxKeepLoggedIn;
    ProgressDialog pdLoggingIn;
    ArrayList<String> alAcceptableUsernames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lgnhndlr.setActivityResponseListener(LoginActivity.this);
        rl = (RelativeLayout)findViewById(R.id.rlLoginLayout);
        bttnLogIn = (Button)findViewById(R.id.bttnLogIn);
        edttxtUsername = (EditText)findViewById(R.id.edttxtUsername);
        edttxtPassword = (EditText)findViewById(R.id.edttxtPassword);
        chckbxKeepLoggedIn = (CheckBox)findViewById(R.id.chckbxKeepLoggedIn);
        pdLoggingIn = new ProgressDialog(LoginActivity.this);
        pdLoggingIn.setMessage("Logging in...");
        alAcceptableUsernames = new ArrayList<>();
        for(int i = 1; i <= 6 ; i++){
            if(i != 2) {
                alAcceptableUsernames.add("TestAPI" + i);
            }
        }
        for (int i = 0; i < rl.getChildCount(); i++) {
            rl.getChildAt(i).clearFocus();
        }
        scaleText();
    }

    public void scaleText() {
        for(int i = 0; i < rl.getChildCount(); i++)
        {
            View v = rl.getChildAt(i);
            if(v.getClass().equals(Button.class))
            {
                ((Button) v).setTextSize(getFontSize(LoginActivity.this,16.0f));
            }
            else if(v.getClass().equals(TextView.class))
            {
                ((TextView) v).setTextSize(getFontSize(LoginActivity.this, 16.0f));
            }
            else if(v.getClass().equals(EditText.class))
            {
                ((EditText) v).setTextSize(getFontSize(LoginActivity.this, 16.0f));
            }
            else if(v.getClass().equals(CheckBox.class))
            {
                ((CheckBox) v).setTextSize(getFontSize(LoginActivity.this, 24.0f));
            }
        }
    }

    public int getFontSize(Activity activity, float fltSize) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

        // lets try to get them back a font size relative to the pixel width of the screen
        final float WIDE = activity.getResources().getDisplayMetrics().widthPixels;
        int valueWide = (int)(WIDE / fltSize / (dMetrics.scaledDensity));
        return valueWide;
    }

    public void logIn(View view){
        for (int i = 0; i < rl.getChildCount(); i++) {
            rl.getChildAt(i).clearFocus();
        }
        if(alAcceptableUsernames.contains(edttxtUsername.getText().toString())) {
            pdLoggingIn.show();
            lgnhndlr.sendLoginRequest(edttxtUsername.getText().toString(), edttxtPassword.getText().toString());
        }
        else{
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(this)
                            .setTitle("Couldn't log in")
                            .setMessage("Invalid username")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();

                                        }
                                    }
                            );
            AlertDialog adError = alertDialogBuilder.show();
        }
    }

    public void responseReceived(String strResponseReceived) {
        pdLoggingIn.hide();
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(this)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        if(strResponseReceived.compareTo("SUCCESS") == 0){
            if(chckbxKeepLoggedIn.isChecked()) {
                // We need an Editor object to make preference changes.
                // All objects are from android.context.Context
                SharedPreferences settings = this.getSharedPreferences("LoginDataFile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", edttxtUsername.getText().toString());
                editor.putString("password", edttxtPassword.getText().toString());
                // Commit the edits!
                editor.apply();
            }
            Intent intnt = new Intent(this, RaceTypeActivity.class);
            startActivity(intnt);
            finish();
        }
        else{
            if(strResponseReceived.compareTo("UnknownHostException") == 0) {
                alertDialogBuilder.setTitle("Couldn't log in");
                alertDialogBuilder.setMessage("Can't connect to Betfair right now. Please check your network connection and try again.");
                AlertDialog adError = alertDialogBuilder.show();
            }
            else if(strResponseReceived.compareTo("SUCCESS") != 0){
                Log.d("thingy", strResponseReceived);
                if(strResponseReceived.compareTo("INVALID_USERNAME_OR_PASSWORD") == 0 || strResponseReceived.compareTo("MULTIPLE_USERS_WITH_SAME_CREDENTIAL") == 0) {
                    alertDialogBuilder.setTitle("Couldn't log in");
                    alertDialogBuilder.setMessage("Invalid username or password.");
                    AlertDialog adError = alertDialogBuilder.show();
                }
                else{
                    alertDialogBuilder.setTitle("Couldn't log in");
                    alertDialogBuilder.setMessage("Something has gone wrong and we couldn't log you in. Please try again soon.");
                    AlertDialog adError = alertDialogBuilder.show();
                }
            }
        }

    }

}
