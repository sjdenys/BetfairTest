package com.example.sjden.betfairtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class LoginActivity extends Activity implements ActivityResponseListener {

    private LoginHandler lgnhndlr = new LoginHandler();
    RelativeLayout rl;
    private Button bttnLogIn;
    private EditText edttxtUsername;
    private EditText edttxtPassword;
    ProgressDialog pdLoggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lgnhndlr.setActivityResponseListener(LoginActivity.this);
        rl = (RelativeLayout)findViewById(R.id.rlLoginLayout);
        bttnLogIn = (Button)findViewById(R.id.bttnLogIn);
        edttxtUsername = (EditText)findViewById(R.id.edttxtUsername);
        edttxtPassword = (EditText)findViewById(R.id.edttxtPassword);
        pdLoggingIn = new ProgressDialog(LoginActivity.this);
        pdLoggingIn.setMessage("Logging in...");
        for (int i = 0; i < rl.getChildCount(); i++) {
            rl.getChildAt(i).clearFocus();
        }
    }

    public void logIn(View view){
        for (int i = 0; i < rl.getChildCount(); i++) {
            rl.getChildAt(i).clearFocus();
        }
        pdLoggingIn.show();
        lgnhndlr.sendLoginRequest(edttxtUsername.getText().toString(), edttxtPassword.getText().toString());
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
            Intent intnt = new Intent(this, RaceTypeActivity.class);
            startActivity(intnt);
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
