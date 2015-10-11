package com.example.sjden.betfairtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.sjden.betfairtest.objects.MarketCatalogue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class RacesActivity extends AppCompatActivity implements ActivityResponseListener {

    private RacesHandler rcshndlr = new RacesHandler();
    private RelativeLayout rl;
    private ProgressBar prgrssbrLoading;
    private TextView txtvwRacesVenueName;
    private ArrayList<Button> raceButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_races);
        getSupportActionBar().setTitle("Betfair");
        initialiseUIElements();
        rcshndlr.setActivityResponseListener(RacesActivity.this);
        this.rcshndlr.setStrEventID(getIntent().getStringExtra("eventID"));
        rcshndlr.sendRequestRaceEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_master, menu);
        menu.findItem(R.id.action_balance).setTitle("AUS: $" + APINGAccountRequester.getDblAusBalance().toString());
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
    public void initialiseUIElements() {
        this.rl = (RelativeLayout) findViewById(R.id.rlRaces);
        this.txtvwRacesVenueName = (TextView) findViewById(R.id.txtvwRacesVenueName);
        this.prgrssbrLoading = (ProgressBar) findViewById(R.id.prgrssbrLoading);
    }

    public void createRaceButtons() {
        raceButtons = new ArrayList<>();
        Button newButton;
        for(int i = 0; i < rcshndlr.getAlRaces().size(); i++) {
            //lets get down to bizness
            newButton = new Button(this);
            newButton.setId(1000 + i);
            newButton.setTag(this.rcshndlr.getAlRaces().get(i).getMarketId().toString());

            newButton.setText(this.rcshndlr.getAlRaces().get(i).getMarketName() + " - " + this.rcshndlr.getAlRaces().get(i).getMarketStartTime());
            newButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnClickDoSomething(v);
                }
            });
            this.raceButtons.add(newButton);
        }
        if(this.raceButtons.size() > 0) {
            rl.addView(this.raceButtons.get(0));
            RelativeLayout.LayoutParams lpTopButton = (RelativeLayout.LayoutParams) this.raceButtons.get(0).getLayoutParams();
            lpTopButton.addRule(RelativeLayout.ALIGN_PARENT_TOP, this.raceButtons.get(0).getId());
            lpTopButton.addRule(RelativeLayout.CENTER_HORIZONTAL, this.raceButtons.get(0).getId());
            for(int i =1; i < this.raceButtons.size(); i++) {
                rl.addView(this.raceButtons.get(i));
                RelativeLayout.LayoutParams lpBelowTopButton = (RelativeLayout.LayoutParams) this.raceButtons.get(i).getLayoutParams();
                lpBelowTopButton.addRule(RelativeLayout.BELOW, this.raceButtons.get(i - 1).getId());
                lpBelowTopButton.addRule(RelativeLayout.ALIGN_LEFT, this.raceButtons.get(i - 1).getId());
                this.raceButtons.get(i).setLayoutParams(lpBelowTopButton);
            }
        }
    }

    public void getOnClickDoSomething(View button)  {
        String strSelectedMarket = button.getTag().toString();
        String strPlaceMarket = "";
        String strRunners = "";
        Date dtWinStartTime = (Date)this.rcshndlr.getHmMarkets().get(strSelectedMarket).get("startTime");
        for (Map.Entry<String, HashMap<String,Object>> entry : this.rcshndlr.getHmMarkets().entrySet()) {
            Date dtPlaceStartTime = (Date)entry.getValue().get("startTime");
            if(entry.getKey().compareTo(strSelectedMarket) != 0 && dtPlaceStartTime.compareTo(dtWinStartTime) == 0){
                strPlaceMarket = entry.getKey();
            }
            else if(entry.getKey().compareTo(strSelectedMarket) == 0){
                strRunners = (String)entry.getValue().get("runners");
            }
            if(strPlaceMarket.compareTo("") != 0 && strRunners.compareTo("") != 0){
                break;
            }
        }
        if(strPlaceMarket.compareTo("") == 0) {
            strPlaceMarket = "noBSP";
        }
        Log.d("thingy",strRunners);
        Intent intntVenues = new Intent(this, RunnersActivity.class);
        intntVenues.putExtra("marketId", strSelectedMarket);
        intntVenues.putExtra("placeMarketId", strPlaceMarket);
        intntVenues.putExtra("runners", strRunners);
        startActivity(intntVenues);
    }

    @Override
    public void responseReceived(String strResponseReceived) {
        if(strResponseReceived.endsWith("Exception")){
            Log.d("thingy", "error");
        }
        else {
            prgrssbrLoading.setVisibility(View.INVISIBLE);
            createRaceButtons();
        }
    }

}
