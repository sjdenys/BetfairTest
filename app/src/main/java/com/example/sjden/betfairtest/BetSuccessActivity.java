package com.example.sjden.betfairtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class BetSuccessActivity extends AppCompatActivity {

    private BetSuccessHandler bsh = new BetSuccessHandler();
    private TextView txtvwRunner;
    private Button bttnSP;
    private TextView txtvwConfirmText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_success);
        getSupportActionBar().setTitle("Betfair");
        this.bsh.setStrRunnerName(getIntent().getStringExtra("runnerName"));
        this.bsh.setDblSP(getIntent().getDoubleExtra("runnerSP", 0.00));
        this.bsh.setDblLiability(Double.parseDouble(getIntent().getStringExtra("liability")));
        initialiseUIElements();
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
            Intent startNewIntent = new Intent(this, WalletActivity.class);
            startActivity(startNewIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialiseUIElements(){
        this.txtvwRunner = (TextView)findViewById(R.id.txtvwRunnerName);
        this.bttnSP = (Button)findViewById(R.id.bttnSP);
        this.txtvwConfirmText = (TextView)findViewById(R.id.txtvwConfirmText);
        this.txtvwRunner.setText(this.bsh.getStrRunnerName());
        DecimalFormat dfFormattedNumbers = new DecimalFormat("#.##");
        if(this.bsh.getDblSP() != 0.0){
            this.bttnSP.setText(dfFormattedNumbers.format(this.bsh.getDblSP()));
        }
        else{
            this.bttnSP.setText("-");
        }
        this.txtvwConfirmText.setText("$" + dfFormattedNumbers.format(this.bsh.getDblLiability()) + " on " + this.bsh.getStrRunnerName());
    }

    @Override
    public void onBackPressed() {
        Intent intntRaceType = new Intent(this, RaceTypeActivity.class);
        intntRaceType.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intntRaceType);
    }

    public void returnToRacing(View vwButton){
        Intent intntRaceType = new Intent(this, RaceTypeActivity.class);
        intntRaceType.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intntRaceType);
    }

    public void showAllBets(View vwButton){
        Intent startNewIntent = new Intent(this, BetlistActivity.class);
        startActivity(startNewIntent);
    }

}
