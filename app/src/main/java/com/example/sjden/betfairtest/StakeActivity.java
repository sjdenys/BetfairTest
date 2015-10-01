package com.example.sjden.betfairtest;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stake);
        this.stkhndlr.setActivityResponseListener(this);
        this.stkhndlr.setStrMarketID(getIntent().getStringExtra("marketID"));
        this.stkhndlr.setStrRunnerID(getIntent().getStringExtra("selectionRunnerId"));
        Log.d("thingy", getIntent().getStringExtra("selectionRunnerId"));
        Log.d("thingy", this.stkhndlr.getStrRunnerID());
        initialiseUIElements();
    }

    private void initialiseUIElements(){
        this.txtvwRunner = (TextView)findViewById(R.id.txtvwRunner);
        this.bttnSP = (Button)findViewById(R.id.bttnSP);
        this.edttxtBetAmount = (EditText)findViewById(R.id.edttxtBetAmount);
        this.txtvwRunner.setText(getIntent().getStringExtra("selectionRunnerName"));
        this.bttnSP.setText(getIntent().getStringExtra("selectionSP"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stake, menu);
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

    public void clickSelectAmount(View view){
        Button bttnClicked = (Button)view;
        this.edttxtBetAmount.setText(bttnClicked.getText().toString().substring(1));
    }

    public void clickPlaceBet(View view){
        this.stkhndlr.sendRequestPlaceOrder(new Double(this.edttxtBetAmount.getText().toString()));
    }

    public void responseReceived(String strResponseReceived){
        if(strResponseReceived.endsWith("Exception")){
            Log.d("thingy","error");
        }
        else {
            Log.d("thingy","success");
        }
    }
}
