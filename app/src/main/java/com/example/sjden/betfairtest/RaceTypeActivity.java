package com.example.sjden.betfairtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class RaceTypeActivity extends AppCompatActivity implements ActivityResponseListener {

    private final RaceTypeHandler rctyphndlr = new RaceTypeHandler();
    private int intHorseCount = -1;
    private int intGreyhoundCount = -1;
    private String strNextPage = "none";
    //UI elements, must be initiated in onCreate()
    private RelativeLayout rl;
    private Button bttnThoroughbreds;
    private Button bttnHarness;
    private Button bttnGreyhounds;
    private ProgressDialog pdLoading;
    private AlertDialog.Builder adbError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_type);
        getSupportActionBar().setTitle("Betfair");
        initialiseUIElements();
        scaleText();
        rctyphndlr.setActivityResponseListener(RaceTypeActivity.this);
        rctyphndlr.sendRequestHorseEvents();
        rctyphndlr.sendRequestGreyhoundEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_master, menu);
        menu.findItem(R.id.action_balance).setTitle("AUS: $" + APINGAccountRequester.getDblAusBalance().toString());
        //getActionBar().setDisplayHomeAsUpEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            // We need an Editor object to make preference changes.
            // All objects are from android.context.Context
            SharedPreferences settings = this.getSharedPreferences("LoginDataFile", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("username");
            editor.remove("password");
            // Commit the edits!
            editor.apply();
            APINGRequester.setStrSessionKey("");
            /** on your logout method:**/
            Intent startNewIntent = new Intent(this, LoginActivity.class);
            startNewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startNewIntent);
            finish();
        }
        else if(id == R.id.action_betlist){
            Intent startNewIntent = new Intent(this, BetlistActivity.class);
            startActivity(startNewIntent);
        }
        else if(id == R.id.action_wallet){
            Log.d("thingy","working");
            Intent startNewIntent = new Intent(this, WalletActivity.class);
            startActivity(startNewIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void initialiseUIElements(){
        this.rl = (RelativeLayout)findViewById(R.id.rlRaceType);
        this.bttnThoroughbreds = (Button)findViewById(R.id.bttnThoroughbreds);
        this.bttnHarness = (Button)findViewById(R.id.bttnHarness);
        this.bttnGreyhounds = (Button)findViewById(R.id.bttnGreyhounds);
        this.pdLoading = new ProgressDialog(RaceTypeActivity.this);
        this.pdLoading.setMessage("Loading...");
        this.adbError = new AlertDialog.Builder(this)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setTitle("No races")
                .setMessage("There are currently no races of that type available. Please try again later.");
    }

    public void scaleText() {
        for(int i = 0; i < rl.getChildCount(); i++)
        {
            View v = rl.getChildAt(i);
            if(v instanceof Button)
            {
                ((Button) v).setTextSize(getFontSize(RaceTypeActivity.this));
            }
        }
    }

    public int getFontSize(Activity activity) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

        // lets try to get them back a font size relative to the pixel width of the screen
        final float WIDE = activity.getResources().getDisplayMetrics().widthPixels;
        int valueWide = (int)(WIDE / 16.0f / (dMetrics.scaledDensity));
        return valueWide;
    }

    public void listThoroughbreds(View view){
        if(this.intHorseCount == -1){
            strNextPage = "thoroughbreds";
            pdLoading.show();
        }
        else if(this.intHorseCount == 0){
            AlertDialog adError = adbError.show();
        }
        else {
            Intent intntVenues = new Intent(this, VenuesActivity.class);
            intntVenues.putExtra("events", rctyphndlr.getStrHorses());
            intntVenues.putExtra("raceType", "thoroughbreds");
            startActivity(intntVenues);
        }
    }

    public void listHarness(View view){
        if(this.intHorseCount == -1){
            strNextPage = "harness";
            pdLoading.show();
        }
        else if(this.intHorseCount == 0){
            AlertDialog adError = adbError.show();
        }
        else {
            Intent intntVenues = new Intent(this, VenuesActivity.class);
            intntVenues.putExtra("events", rctyphndlr.getStrHorses());
            intntVenues.putExtra("raceType", "harness");
            startActivity(intntVenues);
        }
    }

    public void listGreyhounds(View view){
        if(this.intGreyhoundCount == -1){
            strNextPage = "greyhounds";
            pdLoading.show();
        }
        else if(this.intGreyhoundCount == 0){
            AlertDialog adError = adbError.show();
        }
        else {
            Intent intntVenues = new Intent(this, VenuesActivity.class);
            intntVenues.putExtra("events", rctyphndlr.getStrGreyhounds());
            intntVenues.putExtra("raceType", "greyhounds");
            startActivity(intntVenues);
        }
    }

    @Override
    public void responseReceived(String strResponseReceived) {
        if(strResponseReceived.endsWith("Exception")){
            Log.d("thingy",strResponseReceived);
        }
        else {
            responseValueCheck();
        }
    }

    private void responseValueCheck(){
        this.intHorseCount = rctyphndlr.getIntHorseCount();
        this.intGreyhoundCount = rctyphndlr.getIntGreyhoundCount();
        AlertDialog adError;
        switch (strNextPage) {
            case "thoroughbreds":
                switch (intHorseCount) {
                    case -1:
                        break;
                    case 0:
                        pdLoading.hide();
                        adError = adbError.show();
                        break;
                    default:
                        pdLoading.hide();
                        strNextPage = "none";
                        listThoroughbreds(bttnThoroughbreds);
                }
                break;
            case "harness":
                switch (intHorseCount) {
                    case -1:
                        break;
                    case 0:
                        pdLoading.hide();
                        adError = adbError.show();
                        break;
                    default:
                        pdLoading.hide();
                        strNextPage = "none";
                        listHarness(bttnHarness);
                }
                break;
            case "greyhounds":
                switch (intGreyhoundCount) {
                    case -1:
                        break;
                    case 0:
                        pdLoading.hide();
                        adError = adbError.show();
                        break;
                    default:
                        pdLoading.hide();
                        strNextPage = "none";
                        listGreyhounds(bttnGreyhounds);
                }
                break;
            default:
                break;
        }
    }
}
