package com.example.sjden.betfairtest;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.sjden.betfairtest.objects.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VenuesActivity extends AppCompatActivity implements ActivityResponseListener {

    private VenuesHandler vnshndlr = new VenuesHandler();
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        initialiseUIElements();
        vnshndlr.setActivityResponseListener(VenuesActivity.this);
        vnshndlr.setAlevntEvents(loadEventArray((ArrayList<String>)getIntent().getSerializableExtra("events")));
        switch ((String)getIntent().getStringExtra("raceType")){
            case "thoroughbreds":
                vnshndlr.sendRequestThoroughbredMarkets();
                break;
            case "harness":
                vnshndlr.sendRequestHarnessMarkets();
                break;
            case "greyhounds":
                createVenueButtons();
                break;
            default:
                createVenueButtons();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_venue, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void initialiseUIElements(){
        this.rl = (RelativeLayout)findViewById(R.id.rlVenue);
    }

    public ArrayList<Event> loadEventArray(ArrayList<String> alstrEvents){
        ArrayList<Event> alevntEvents = new ArrayList<>();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        for(String s : alstrEvents){
            alevntEvents.add(gson.fromJson(s, Event.class));
        }
        return alevntEvents;
    }

    public void createVenueButtons(){
        ArrayList<Button> albttnVenues = new ArrayList<>();
        Button bttnForArray;

        for(int i = 0 ; i < vnshndlr.getAlevntTodayEvents().size() ; i++){
            if(vnshndlr.getAlevntTodayEvents().get(i).getVenue() != null && !vnshndlr.getAlevntTodayEvents().get(i).getName().contains("(AvB)")) {
                bttnForArray = new Button(this);
                bttnForArray.setTag("bttn" + vnshndlr.getAlevntTodayEvents().get(i).getVenue());
                bttnForArray.setText(vnshndlr.getAlevntTodayEvents().get(i).getVenue());
                bttnForArray.setId(1000 + i);
                if (vnshndlr.getAlevntTodayEvents().get(i).getCountryCode().compareTo("NZ") == 0) {
                    bttnForArray.setText(bttnForArray.getText() + " (" + vnshndlr.getAlevntTodayEvents().get(i).getCountryCode() + ")");
                }
                albttnVenues.add(bttnForArray);
            }
        }

        rl.addView(albttnVenues.get(0));
        RelativeLayout.LayoutParams lpTopButton = (RelativeLayout.LayoutParams)albttnVenues.get(0).getLayoutParams();
        lpTopButton.addRule(RelativeLayout.ALIGN_PARENT_TOP, albttnVenues.get(0).getId());
        lpTopButton.addRule(RelativeLayout.CENTER_HORIZONTAL, albttnVenues.get(0).getId());

        for(int i = 1 ; i < albttnVenues.size() ; i++){
            rl.addView(albttnVenues.get(i));
            RelativeLayout.LayoutParams lpBelowTopButton = (RelativeLayout.LayoutParams)albttnVenues.get(i).getLayoutParams();
            lpBelowTopButton.addRule(RelativeLayout.BELOW, albttnVenues.get(i - 1).getId());
            lpBelowTopButton.addRule(RelativeLayout.ALIGN_LEFT, albttnVenues.get(i - 1).getId());
            albttnVenues.get(i).setLayoutParams(lpBelowTopButton);
        }
    }

    @Override
    public void responseReceived(String strResponseReceived) {
        if(strResponseReceived.endsWith("Exception")){
            Log.d("thingy","error");
        }
        else {
            createVenueButtons();
        }
    }
}
