package com.example.sjden.betfairtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sjden.betfairtest.objects.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;


public class VenuesActivity extends AppCompatActivity implements ActivityResponseListener, AdapterView.OnItemSelectedListener {

    private VenuesHandler vnshndlr = new VenuesHandler();
    private String strCurrentRaceDate = "Today";
    private boolean boolFirstRun = true;
    private ArrayList<Button> albttnVenues = new ArrayList<>();
    private RelativeLayout rl;
    private TextView txtvwNoRacesToday;
    private TextView txtvwNoRacesTomorrow;
    private Spinner spnnrRaceDates;
    private ProgressBar prgrssbrLoadingHorses;

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
        getMenuInflater().inflate(R.menu.menu_master, menu);
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
        this.rl = (RelativeLayout)findViewById(R.id.rlContent);
        this.txtvwNoRacesToday = (TextView)findViewById(R.id.txtvwNoRacesToday);
        this.txtvwNoRacesTomorrow = (TextView)findViewById(R.id.txtvwNoRacesTomorrow);
        this.spnnrRaceDates = (Spinner) findViewById(R.id.spnnrRaceDates);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.race_date_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        this.spnnrRaceDates.setAdapter(adapter);
        this.spnnrRaceDates.setOnItemSelectedListener(this);
        this.prgrssbrLoadingHorses = (ProgressBar)findViewById(R.id.prgrssbrLoadingHorses);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(!boolFirstRun) {
            for (Button b : this.albttnVenues) {
                this.rl.removeView(b);
            }
            this.strCurrentRaceDate = this.spnnrRaceDates.getSelectedItem().toString();
            createVenueButtons();
            }
        else{
            boolFirstRun = false;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("thingy", "none");
    }

    public void scaleText() {
        for(int i = 0; i < rl.getChildCount(); i++)
        {
            View v = rl.getChildAt(i);
            if(v.getClass().equals(Button.class))
            {
                ((Button) v).setTextSize(getFontSize(VenuesActivity.this,16.0f));
            }
            else if(v.getClass().equals(TextView.class))
            {
                ((TextView) v).setTextSize(getFontSize(VenuesActivity.this, 16.0f));
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

    public ArrayList<Event> loadEventArray(ArrayList<String> alstrEvents){
        ArrayList<Event> alevntEvents = new ArrayList<>();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        for(String s : alstrEvents){
            alevntEvents.add(gson.fromJson(s, Event.class));
        }
        return alevntEvents;
    }

    public void createVenueButtons(){
        ArrayList<Event> alEvents = new ArrayList<>();
        alEvents.clear();
        albttnVenues.clear();
        this.txtvwNoRacesToday.setVisibility(View.INVISIBLE);
        this.txtvwNoRacesTomorrow.setVisibility(View.INVISIBLE);
        Button bttnForArray;

        if(this.strCurrentRaceDate.compareTo("Today") == 0){
            alEvents = new ArrayList<>(vnshndlr.getAlevntTodayEvents());
        }
        else if(this.strCurrentRaceDate.compareTo("Tomorrow") == 0){
            alEvents = new ArrayList<>(vnshndlr.getAlevntTomorrowEvents());
        }

        for(int i = 0 ; i < alEvents.size() ; i++){
            if(alEvents.get(i).getVenue() != null) {
                bttnForArray = new Button(this);
                bttnForArray.setId(Integer.parseInt(alEvents.get(i).getId()));
                bttnForArray.setTag("bttn" + alEvents.get(i).getVenue());
                bttnForArray.setText(alEvents.get(i).getVenue());
                bttnForArray.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        getOnClickDoSomething(v);
                    }
                });
                if (alEvents.get(i).getCountryCode().compareTo("NZ") == 0) {
                    bttnForArray.setText(bttnForArray.getText() + " (" + alEvents.get(i).getCountryCode() + ")");
                }
                this.albttnVenues.add(bttnForArray);
            }
        }

        if(this.albttnVenues.size() > 0) {
            rl.addView(this.albttnVenues.get(0));
            RelativeLayout.LayoutParams lpTopButton = (RelativeLayout.LayoutParams) this.albttnVenues.get(0).getLayoutParams();
            lpTopButton.addRule(RelativeLayout.BELOW, this.spnnrRaceDates.getId());
            lpTopButton.addRule(RelativeLayout.CENTER_HORIZONTAL, this.albttnVenues.get(0).getId());
            lpTopButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, this.albttnVenues.get(0).getId());
            lpTopButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, this.albttnVenues.get(0).getId());
            for (int i = 1; i < this.albttnVenues.size(); i++) {
                rl.addView(this.albttnVenues.get(i));
                RelativeLayout.LayoutParams lpBelowTopButton = (RelativeLayout.LayoutParams) this.albttnVenues.get(i).getLayoutParams();
                lpBelowTopButton.addRule(RelativeLayout.BELOW, this.albttnVenues.get(i - 1).getId());
                lpBelowTopButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, this.albttnVenues.get(i).getId());
                lpBelowTopButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, this.albttnVenues.get(i).getId());
                this.albttnVenues.get(i).setLayoutParams(lpBelowTopButton);
            }
        }
        else{
            if(this.strCurrentRaceDate.compareTo("Today") == 0){
                this.txtvwNoRacesToday.setVisibility(View.VISIBLE);
            }
            else if(this.strCurrentRaceDate.compareTo("Tomorrow") == 0){
                this.txtvwNoRacesTomorrow.setVisibility(View.VISIBLE);
            }
        }

        this.spnnrRaceDates.setVisibility(View.VISIBLE);
        this.prgrssbrLoadingHorses.setVisibility(View.INVISIBLE);
        scaleText();
    }

    public void getOnClickDoSomething(View button)  {
        Intent intntVenues = new Intent(this, RacesActivity.class);
        intntVenues.putExtra("eventID", Integer.toString(button.getId()));
        startActivity(intntVenues);
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
