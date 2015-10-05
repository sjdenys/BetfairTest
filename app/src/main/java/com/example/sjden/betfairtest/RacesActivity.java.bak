package com.example.sjden.betfairtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class RacesActivity extends AppCompatActivity implements ActivityResponseListener {

    private final RacesHandler rchndlr = new RacesHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_races);
        this.rchndlr.setActivityResponseListener(RacesActivity.this);
        this.rchndlr.setStrEventId((String) getIntent().getStringExtra("eventID"));
        this.rchndlr.sendRequestMarkets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_master, menu);
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

    @Override
    public void responseReceived(String strResponseReceived) {
        if(strResponseReceived.endsWith("Exception")){
            Log.d("thingy", "error");
        }
        else {
            Intent intntRunners = new Intent(this, RunnersActivity.class);
            intntRunners.putExtra("market", this.rchndlr.getStrMarket());
            startActivity(intntRunners);
        }
    }

}
