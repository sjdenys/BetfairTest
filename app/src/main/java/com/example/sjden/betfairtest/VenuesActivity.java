package com.example.sjden.betfairtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sjden.betfairtest.objects.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;


public class VenuesActivity extends AppCompatActivity implements ActivityResponseListener, AdapterView.OnItemSelectedListener {

    private VenuesHandler vnshndlr = new VenuesHandler();
    private RelativeLayout rl;
    private TextView txtvwNoRacesToday;
    private Spinner spnnrRaceDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        initialiseUIElements();
        vnshndlr.setActivityResponseListener(VenuesActivity.this);
        vnshndlr.setAlevntEvents(loadEventArray((ArrayList<String>) getIntent().getSerializableExtra("events")));
        switch ((String)getIntent().getStringExtra("raceType")){
            case "thoroughbreds":
                vnshndlr.sendRequestThoroughbredMarkets();
                break;
            case "harness":
                vnshndlr.sendRequestHarnessMarkets();
                break;
            case "greyhounds":
                vnshndlr.sortAndDivideEvents();
                createVenueButtons();
                break;
            default:
                vnshndlr.sortAndDivideEvents();
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
        this.txtvwNoRacesToday = (TextView)findViewById(R.id.txtvwNoRacesToday);
        this.spnnrRaceDates =  (Spinner) findViewById(R.id.spnnrRaceDates);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.race_date_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        this.spnnrRaceDates.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.d("thingy", parent.getItemAtPosition(pos).toString());
    }

    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("thingy", "none");
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
            if(vnshndlr.getAlevntTodayEvents().get(i).getVenue() != null) {
                bttnForArray = new Button(this);
                bttnForArray.setId(Integer.parseInt(vnshndlr.getAlevntTodayEvents().get(i).getId()));
                bttnForArray.setTag("bttn" + vnshndlr.getAlevntTodayEvents().get(i).getVenue());
                bttnForArray.setText(vnshndlr.getAlevntTodayEvents().get(i).getVenue());
                bttnForArray.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        getOnClickDoSomething(v);
                    }
                });
                if (vnshndlr.getAlevntTodayEvents().get(i).getCountryCode().compareTo("NZ") == 0) {
                    bttnForArray.setText(bttnForArray.getText() + " (" + vnshndlr.getAlevntTodayEvents().get(i).getCountryCode() + ")");
                    bttnForArray.setText(bttnForArray.getText() + " (" + vnshndlr.getAlevntTodayEvents().get(i).getCountryCode() + ")");
                }
                albttnVenues.add(bttnForArray);
            }
        }
        if(albttnVenues.size() > 0) {
            rl.addView(albttnVenues.get(0));
            RelativeLayout.LayoutParams lpTopButton = (RelativeLayout.LayoutParams) albttnVenues.get(0).getLayoutParams();
            lpTopButton.addRule(RelativeLayout.ALIGN_PARENT_TOP, albttnVenues.get(0).getId());
            lpTopButton.addRule(RelativeLayout.CENTER_HORIZONTAL, albttnVenues.get(0).getId());
            for (int i = 1; i < albttnVenues.size(); i++) {
                rl.addView(albttnVenues.get(i));
                RelativeLayout.LayoutParams lpBelowTopButton = (RelativeLayout.LayoutParams) albttnVenues.get(i).getLayoutParams();
                lpBelowTopButton.addRule(RelativeLayout.BELOW, albttnVenues.get(i - 1).getId());
                lpBelowTopButton.addRule(RelativeLayout.ALIGN_LEFT, albttnVenues.get(i - 1).getId());
                albttnVenues.get(i).setLayoutParams(lpBelowTopButton);
            }
        }
        else{
            txtvwNoRacesToday.setVisibility(View.VISIBLE);
        }
    }

    public void getOnClickDoSomething(View button)  {
        Log.d("thingy","ham");
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
