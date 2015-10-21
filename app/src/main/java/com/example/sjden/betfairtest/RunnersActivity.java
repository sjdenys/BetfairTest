package com.example.sjden.betfairtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sjden.betfairtest.objects.MarketCatalogue;
import com.example.sjden.betfairtest.objects.Runner;
import com.example.sjden.betfairtest.objects.RunnerCatalog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RunnersActivity extends AppCompatActivity implements ActivityResponseListener, AdapterView.OnItemSelectedListener {

    private final RunnersHandler rnnrshndlr = new RunnersHandler();
    private ListView lstvwRunners;
    private ArrayAdapter<RunnerCatalog> adapter;
    private Spinner spnnrMarket;
    private boolean boolFirstRun = true;
    private ProgressBar prgrssbrLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runners);
        getSupportActionBar().setTitle("Betfair");
        initialiseUIElements();
        this.rnnrshndlr.setActivityResponseListener(RunnersActivity.this);
        parseMarkets();
        this.rnnrshndlr.sendRequestMarketBook();
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

    public void initialiseUIElements(){
        this.prgrssbrLoading = (ProgressBar)findViewById(R.id.prgrssbrLoading);
        this.lstvwRunners = (ListView)findViewById(R.id.lstvwRunners);
        this.spnnrMarket = (Spinner) findViewById(R.id.spnnrMarket);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.market_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        this.spnnrMarket.setAdapter(adapter);
        this.spnnrMarket.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(!boolFirstRun) {
            if(this.spnnrMarket.getSelectedItem().toString().compareTo("Win") == 0) {
                this.adapter = new RunnersArrayAdapter(this, this.rnnrshndlr.getAlrcRunnerCatalog(), this.rnnrshndlr.getAlrnnrRunners());
                this.rnnrshndlr.setStrMarketId(this.rnnrshndlr.getStrWinMarketId());
            }
            else{
                this.adapter = new RunnersArrayAdapter(this, this.rnnrshndlr.getAlrcRunnerCatalog(), this.rnnrshndlr.getAlrnnrPlaceRunners());
                this.rnnrshndlr.setStrMarketId(this.rnnrshndlr.getStrPlaceMarketId());
            }
            this.lstvwRunners.setAdapter(adapter);
        }
        else{
            boolFirstRun = false;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("thingy", "none");
    }

    public void getOnClickDoSomething(View button)  {
        if(this.rnnrshndlr.getStrMarketStatus().compareToIgnoreCase("open") != 0){
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(this)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setTitle("Error")
                            .setMessage("This market is no longer open.");
            AlertDialog adError = alertDialogBuilder.show();
        }
        else {
            Intent intntVenues = new Intent(this, StakeActivity.class);
            RunnerCatalog rcSelection = null;
            Runner rnnrSelection = null;
            for (RunnerCatalog rc : this.rnnrshndlr.getAlrcRunnerCatalog()) {
                if (rc.getSelectionId().toString().compareTo(button.getTag().toString()) == 0) {
                    rcSelection = rc;
                }
            }
            ArrayList<Runner> alrRunner;
            if (this.spnnrMarket.getSelectedItem().toString().compareToIgnoreCase("Win") == 0) {
                alrRunner = this.rnnrshndlr.getAlrnnrRunners();
            } else {
                alrRunner = this.rnnrshndlr.getAlrnnrPlaceRunners();
            }
            for (Runner r : alrRunner) {
                if (r.getSelectionId().toString().compareTo(button.getTag().toString()) == 0) {
                    rnnrSelection = r;
                }
            }
            Log.d("thingy", this.rnnrshndlr.getStrMarketId());
            intntVenues.putExtra("marketID", this.rnnrshndlr.getStrMarketId());
            intntVenues.putExtra("selectionRunnerName", rcSelection.getRunnerName());
            intntVenues.putExtra("selectionRunnerId", rcSelection.getSelectionId().toString());
            if (rnnrSelection.getSp() != null && rnnrSelection.getSp().getNearPrice() != null) {
                Log.d("thingy","1");
                intntVenues.putExtra("selectionSP", rnnrSelection.getSp().getNearPrice().toString());
            } else {
                Log.d("thingy","2");
                intntVenues.putExtra("selectionSP", "0.0");
            }
            startActivity(intntVenues);
        }
    }

    private void parseMarkets(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        try {
            JSONArray jArray = new JSONArray((String) getIntent().getStringExtra("runners"));
            this.rnnrshndlr.setStrMarketId((String) getIntent().getStringExtra("marketId"));
            this.rnnrshndlr.setStrWinMarketId((String) getIntent().getStringExtra("marketId"));
            this.rnnrshndlr.setStrPlaceMarketId((String) getIntent().getStringExtra("placeMarketId"));
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    this.rnnrshndlr.getAlrcRunnerCatalog().add(gson.fromJson(jArray.getJSONObject(i).toString(), RunnerCatalog.class));
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        catch(Exception e){

        }
        ArrayList<String> alstrRunnerName = new ArrayList<>();
        for(RunnerCatalog r :  this.rnnrshndlr.getAlrcRunnerCatalog()){
            alstrRunnerName.add(r.getRunnerName());
        }
    }

    @Override
    public void responseReceived(Object objResponseReceived){
        if(objResponseReceived.getClass() == Exception.class){
            Log.d("thingy", "problem");
        }
        else if(objResponseReceived.getClass() == String.class){
            if(this.rnnrshndlr.getAlrnnrPlaceRunners().size() == 0){
                this.spnnrMarket.setEnabled(false);
            }
            this.spnnrMarket.setVisibility(View.VISIBLE);
            this.prgrssbrLoading.setVisibility(View.INVISIBLE);
            if(this.rnnrshndlr.getStrMarketStatus().compareToIgnoreCase("open") != 0){
                this.spnnrMarket.setEnabled(false);
                AlertDialog.Builder alertDialogBuilder =
                        new AlertDialog.Builder(this)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                        .setTitle("Error")
                        .setMessage("This market is no longer open.");
                AlertDialog adError = alertDialogBuilder.show();
            }
            this.adapter = new RunnersArrayAdapter(this, this.rnnrshndlr.getAlrcRunnerCatalog(), this.rnnrshndlr.getAlrnnrRunners());
            this.lstvwRunners.setAdapter(adapter);
        }
    }

    public View.OnClickListener myClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            getOnClickDoSomething(v);
        }
    };

    public class RunnersArrayAdapter extends ArrayAdapter<RunnerCatalog> {
        private final Context context;
        private final ArrayList<RunnerCatalog> alrnnrRunnersCatalog;
        private final ArrayList<Runner> alrnnrRunners;

        public RunnersArrayAdapter(Context context, ArrayList<RunnerCatalog> alrnnrRunnersCatalog, ArrayList<Runner> alrnnrRunners) {
            super(context, -1, alrnnrRunnersCatalog);
            this.context = context;
            this.alrnnrRunnersCatalog = alrnnrRunnersCatalog;
            this.alrnnrRunners = alrnnrRunners;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.listrow_runners, parent, false);
            rowView.setClickable(true);
            rowView.setOnClickListener(myClickListener);
            rowView.setTag(Long.toString(alrnnrRunnersCatalog.get(position).getSelectionId()));
            TextView textView = (TextView) rowView.findViewById(R.id.txtvwRunnerName);
            Button imageView = (Button) rowView.findViewById(R.id.bttnSP);
            imageView.setOnClickListener(myClickListener);
            imageView.setTag(Long.toString(alrnnrRunnersCatalog.get(position).getSelectionId()));
            String s = alrnnrRunnersCatalog.get(position).getRunnerName();
            textView.setText(s);
            for(Runner r : alrnnrRunners){
                if(r.getSelectionId().compareTo(alrnnrRunnersCatalog.get(position).getSelectionId()) == 0) {
                    if (r.getStatus().compareToIgnoreCase("Active") != 0){
                        textView.setTextColor(Color.GRAY);
                        imageView.setText("Scratched");
                        imageView.setEnabled(false);
                    }
                    else if(r.getSp() == null || r.getSp().getNearPrice() == null || r.getSp().getNearPrice().isNaN() || r.getSp().getNearPrice() == Double.POSITIVE_INFINITY){
                        imageView.setText("-");
                    }
                    else {
                        DecimalFormat dfSP = new DecimalFormat("#.##");
                        imageView.setText(dfSP.format(r.getSp().getNearPrice()));
                    }
                }
            }

            return rowView;
        }
    }

}
