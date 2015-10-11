package com.example.sjden.betfairtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.view.View;
import android.widget.TextView;

public class WalletActivity extends AppCompatActivity implements ActivityResponseListener{
    private WalletHandler walletHandler = new WalletHandler();
    private RelativeLayout rl;
    private TextView ukBalanceText;
    private TextView txtvwUKBalanceLabel;
    private TextView txtvwAusBalanceLabel;
    private TextView txtvwAmountLabel;
    private TextView ausBalanceText;
    private Button transferButton;
    private EditText transferAmount;
    private Switch directionSwitch;
    private ProgressBar prgrssbrLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        walletHandler.setActivityResponseListener(WalletActivity.this);
        walletHandler.requestUKAccountFunds();
        walletHandler.requestAUSAccountFunds();
        this.rl = (RelativeLayout)findViewById(R.id.rlContent);
        this.ukBalanceText = (TextView)findViewById(R.id.ukBalanceText);
        this.ausBalanceText = (TextView)findViewById(R.id.ausBalanceText);
        this.txtvwUKBalanceLabel = (TextView)findViewById(R.id.txtvwUKBalanceLabel);
        this.txtvwAusBalanceLabel = (TextView)findViewById(R.id.txtvwAusBalanceLabel);
        this.txtvwAmountLabel = (TextView)findViewById(R.id.txtvwAmountLabel);
        this.transferButton = (Button)findViewById(R.id.transferButton);
        this.transferAmount = (EditText)findViewById(R.id.amountText);
        this.directionSwitch = (Switch)findViewById(R.id.directionSwitch);
        this.prgrssbrLoading = (ProgressBar)findViewById(R.id.prgrssbrLoading);
    }

    public void doTransfer(View view) {
        Log.d("Button Pressed", "Do transfer");
        //ukBalanceText.setText(walletHandler.ukBalString);
        //ausBalanceText.setText(walletHandler.ausBalString);
        if(transferAmount.getText().toString() != null && transferAmount.getText().toString().isEmpty() == false) {
            Double amount = Double.parseDouble(transferAmount.getText().toString());
            if (directionSwitch.isChecked()) {
                //UK > AUS
                walletHandler.doTransfer("UK>AUS", amount);
            } else {
                //AUS > UK
                walletHandler.doTransfer("AUS>UK", amount);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_master, menu);
        menu.findItem(R.id.action_balance).setTitle("AUS: $" + APINGAccountRequester.getDblAusBalance().toString());
        return true;
    }

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

    @Override
    public void responseReceived(String strResponseReceived) {
        if(strResponseReceived.endsWith("Exception")){
            Log.d("Exception", strResponseReceived);
        }
        else {
            Log.d("WalActResponseReceived", strResponseReceived);
            prgrssbrLoading.setVisibility(View.INVISIBLE);
            this.ukBalanceText.setVisibility(View.VISIBLE);
            this.ausBalanceText.setVisibility(View.VISIBLE);
            this.transferButton.setVisibility(View.VISIBLE);
            this.transferAmount.setVisibility(View.VISIBLE);
            this.directionSwitch.setVisibility(View.VISIBLE);
            this.txtvwUKBalanceLabel.setVisibility(View.VISIBLE);
            this.txtvwAusBalanceLabel.setVisibility(View.VISIBLE);
            this.txtvwAmountLabel.setVisibility(View.VISIBLE);
            ukBalanceText.setText(walletHandler.ukBalString);
            ausBalanceText.setText(walletHandler.ausBalString);
        }
    }
}
