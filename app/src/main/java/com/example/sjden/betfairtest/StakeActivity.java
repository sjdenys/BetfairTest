package com.example.sjden.betfairtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StakeActivity extends AppCompatActivity implements ActivityResponseListener {

    private StakeHandler stkhndlr = new StakeHandler();
    private TextView txtvwRunner;
    private Button bttnSP;
    private EditText edttxtBetAmount;
    private ProgressDialog pdLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stake);
        getSupportActionBar().setTitle("Betfair");
        this.stkhndlr.setActivityResponseListener(this);
        this.stkhndlr.setStrMarketID(getIntent().getStringExtra("marketID"));
        this.stkhndlr.setStrRunnerID(getIntent().getStringExtra("selectionRunnerId"));
        this.stkhndlr.setStrRunnerName(getIntent().getStringExtra("selectionRunnerName"));
        this.stkhndlr.setDblSP(Double.parseDouble(getIntent().getStringExtra("selectionSP")));
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
            Log.d("thingy","working");
            Intent startNewIntent = new Intent(this, WalletActivity.class);
            startActivity(startNewIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialiseUIElements(){
        this.txtvwRunner = (TextView)findViewById(R.id.txtvwRunner);
        this.bttnSP = (Button)findViewById(R.id.bttnSP);
        this.edttxtBetAmount = (EditText)findViewById(R.id.edttxtBetAmount);
        this.txtvwRunner.setText(this.stkhndlr.getStrRunnerName());
        DecimalFormat dfFormattedSP = new DecimalFormat("#.##");
        this.bttnSP.setText(dfFormattedSP.format(this.stkhndlr.getDblSP()));
        this.pdLoading = new ProgressDialog(StakeActivity.this);
        this.pdLoading.setMessage("Placing bet...");
        this.pdLoading.setCancelable(false);
    }

    public void clickSelectAmount(View view){
        Button bttnClicked = (Button)view;
        this.edttxtBetAmount.setText(bttnClicked.getText().toString().substring(1));
    }

    public void clickPlaceBet(View view){
        this.pdLoading.show();
        this.stkhndlr.sendRequestPlaceOrder(new Double(this.edttxtBetAmount.getText().toString()));
    }

    public void responseReceived(String strResponseReceived){
        if(strResponseReceived.endsWith("Exception")){
            Log.d("thingy","exception");
        }
        else if(strResponseReceived.compareToIgnoreCase("success") == 0){
            Log.d("thingy", "success");
            APINGAccountRequester.setDblAusBalance(APINGAccountRequester.getDblAusBalance() - Double.parseDouble(this.edttxtBetAmount.getText().toString()));
            Intent intntBetSuccess = new Intent(this, BetSuccessActivity.class);
            intntBetSuccess.putExtra("runnerName",this.stkhndlr.getStrRunnerName());
            intntBetSuccess.putExtra("runnerSP",this.stkhndlr.getDblSP());
            intntBetSuccess.putExtra("liability",this.edttxtBetAmount.getText().toString());
            startActivity(intntBetSuccess);
        }
        else{
            this.pdLoading.hide();
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(this)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            if(strResponseReceived.compareToIgnoreCase("INSUFFICIENT_FUNDS") == 0){
                alertDialogBuilder.setTitle("Insufficient funds");
                alertDialogBuilder.setMessage("You do not have sufficient funds to place this bet.");
            }
            else{
                alertDialogBuilder.setTitle("Couldn't place bet");
                alertDialogBuilder.setMessage("Something has gone wrong and we couldn't place your bet. Please try again soon.");
            }
            AlertDialog adError = alertDialogBuilder.show();
        }
    }
}
