package com.example.sjden.betfairtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RaceTypeActivity extends AppCompatActivity implements ActivityResponseListener {

    private RaceTypeHandler rctyphndlr = new RaceTypeHandler();
    private Button bttnThoroughbreds;
    private Button bttnHarness;
    private Button bttnGreyhounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_type);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rlRaceType);
        bttnThoroughbreds = (Button)findViewById(R.id.bttnThoroughbreds);
        bttnHarness = (Button)findViewById(R.id.bttnHarness);
        bttnGreyhounds = (Button)findViewById(R.id.bttnGreyhounds);
        int count = rl.getChildCount();
        for(int i = 0; i < count; i++)
        {
            View v = rl.getChildAt(i);
            if(v instanceof Button)
            {
                ((Button) v).setTextSize(getFontSize(RaceTypeActivity.this));
            }
        }
        rctyphndlr.setActivityResponseListener(RaceTypeActivity.this);
        rctyphndlr.sendRequestHorseEvents();
        rctyphndlr.sendRequestGreyhoundEvents();
    }

    public void listThoroughbreds(View view){
        Intent intnt = new Intent(this, VenuesActivity.class);
        intnt.putExtra("events",rctyphndlr.getStrHorses());
        intnt.putExtra("raceType", "thoroughbreds");
        startActivity(intnt);
    }

    public void listHarness(View view){
        Intent intnt = new Intent(this, VenuesActivity.class);
        intnt.putExtra("events",rctyphndlr.getStrHorses());
        intnt.putExtra("raceType", "harness");
        startActivity(intnt);
    }

    public void listGreyhounds(View view){
        Intent intnt = new Intent(this, VenuesActivity.class);
        intnt.putExtra("events",rctyphndlr.getStrGreyhounds());
        intnt.putExtra("raceType", "greyhounds");
        startActivity(intnt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_race_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int getFontSize(Activity activity) {

        DisplayMetrics dMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

        // lets try to get them back a font size realtive to the pixel width of the screen
        final float WIDE = activity.getResources().getDisplayMetrics().widthPixels;
        int valueWide = (int)(WIDE / 16.0f / (dMetrics.scaledDensity));
        return valueWide;
    }

    @Override
    public void responseReceived(String strResponseReceived) {

    }
}
