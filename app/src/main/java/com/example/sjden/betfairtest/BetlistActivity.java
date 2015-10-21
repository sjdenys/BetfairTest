package com.example.sjden.betfairtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class BetlistActivity extends AppCompatActivity implements ActivityResponseListener{

    private BetlistHandler handler = new BetlistHandler();
    public List<String> simpleValues;
    private ListView lstvwRunners;
    private ArrayAdapter<BetlistHandler.OrderForList> adapter;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betlist);
        simpleValues = new ArrayList<String>();
        this.handler.setActivityResponseListener(this);
        handler.sendRequestCurrentOrders();
        this.lstvwRunners = (ListView)findViewById(R.id.lstvwBetList);
        this.pbLoading = (ProgressBar)findViewById(R.id.pbLoading);
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

    @Override
    public void responseReceived(Object objResponseReceived) {
        if(objResponseReceived.getClass() == Exception.class){
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(this)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            alertDialogBuilder.setTitle("Couldn't show bet history");
            alertDialogBuilder.setMessage("Something has gone wrong and we couldn't show your bet history. Please try again soon.");
            AlertDialog adError = alertDialogBuilder.show();
        }
        else if(objResponseReceived.getClass() == String.class){
            this.pbLoading.setVisibility(View.INVISIBLE);
            this.adapter = new BetlistArrayAdapter(this, this.handler.getAloflBetList());
            this.lstvwRunners.setAdapter(adapter);
        }
    }

    public class BetlistArrayAdapter extends ArrayAdapter<BetlistHandler.OrderForList> {
        private final Context context;
        private final ArrayList<BetlistHandler.OrderForList> alrnnrOrders;

        public BetlistArrayAdapter(Context context, ArrayList<BetlistHandler.OrderForList> alrnnrOrders) {
            super(context, -1, alrnnrOrders);
            this.context = context;
            this.alrnnrOrders = alrnnrOrders;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.listrow_betlist, parent, false);
            rowView.setTag(alrnnrOrders.get(position).getStrRunnerName());
            TextView txtvwRunnerName = (TextView) rowView.findViewById(R.id.txtvwRunnerName);
            TextView txtvwStatus = (TextView) rowView.findViewById(R.id.txtvwStatus);
            TextView txtvwProfit = (TextView) rowView.findViewById(R.id.txtvwProfit);
            TextView txtvwRace = (TextView) rowView.findViewById(R.id.txtvwRace);
            TextView txtvwDatePlaced = (TextView) rowView.findViewById(R.id.txtvwDatePlaced);
            txtvwRunnerName.setText(alrnnrOrders.get(position).getStrRunnerName() + " - $" + Double.toString(alrnnrOrders.get(position).getDblStake()));
            txtvwStatus.setText(alrnnrOrders.get(position).getStrBetStatus());
            txtvwProfit.setText(Double.toString(alrnnrOrders.get(position).getDblProfit()));
            if(alrnnrOrders.get(position).getStrBetStatus().compareToIgnoreCase("won") == 0){
                txtvwStatus.setTextColor(Color.GREEN);
                txtvwProfit.setTextColor(Color.GREEN);
            }
            else if(alrnnrOrders.get(position).getStrBetStatus().compareToIgnoreCase("lost") == 0){
                txtvwStatus.setTextColor(Color.RED);
                txtvwProfit.setTextColor(Color.RED);
            }
            txtvwRace.setText(alrnnrOrders.get(position).getStrRace());
            DateTime dtMarketStartTime = new DateTime(alrnnrOrders.get(position).getDtDatePlaced());
            DateTimeZone zone = DateTimeZone.forID("Australia/Melbourne");
            dtMarketStartTime = dtMarketStartTime.plus(zone.getOffset(DateTime.now()));
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yy HH:mm");
            txtvwDatePlaced.setText(dtfOut.print(dtMarketStartTime));
            return rowView;
        }
    }
}
