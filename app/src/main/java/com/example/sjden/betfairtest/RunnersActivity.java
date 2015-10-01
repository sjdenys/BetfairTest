package com.example.sjden.betfairtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
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

public class RunnersActivity extends AppCompatActivity implements ActivityResponseListener {

    private final RunnersHandler rnnrshndlr = new RunnersHandler();
    private ListView lstvwRunners;
    private ArrayAdapter<RunnerCatalog> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runners);
        initialiseUIElements();
        this.rnnrshndlr.setActivityResponseListener(RunnersActivity.this);
        parseMarkets();
        this.rnnrshndlr.sendRequestMarketBook();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_runners, menu);
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

    public void initialiseUIElements(){
        this.lstvwRunners = (ListView)findViewById(R.id.lstvwRunners);
    }

    public void getOnClickDoSomething(View button)  {
        Intent intntVenues = new Intent(this, StakeActivity.class);
        RunnerCatalog rcSelection = null;
        Runner rnnrSelection = null;
        for(RunnerCatalog rc : this.rnnrshndlr.getAlrcRunnerCatalog()){
            if(rc.getSelectionId().toString().compareTo(button.getTag().toString()) == 0){
                rcSelection = rc;
            }
        }
        for(Runner r : this.rnnrshndlr.getAlrnnrRunners()){
            if(r.getSelectionId().toString().compareTo(button.getTag().toString()) == 0){
                rnnrSelection = r;
            }
        }

        intntVenues.putExtra("marketID",this.rnnrshndlr.getStrMarketId());
        intntVenues.putExtra("selectionRunnerName", rcSelection.getRunnerName());
        intntVenues.putExtra("selectionRunnerId", rcSelection.getSelectionId().toString());
        if(rnnrSelection.getSp() != null) {
            intntVenues.putExtra("selectionSP", rnnrSelection.getSp().getNearPrice().toString());
        }
        else{
            intntVenues.putExtra("selectionSP", "-");
        }
        startActivity(intntVenues);
    }

    private void parseMarkets(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        try {
            JSONObject jObject = new JSONObject((String) getIntent().getStringExtra("market"));
            this.rnnrshndlr.setStrMarketId(jObject.getString("marketId"));
            JSONArray jArray = jObject.getJSONArray("runners");
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
    public void responseReceived(String strResponseReceived){
        if(strResponseReceived.endsWith("Exception")){
            Log.d("thingy", "problem");
        }
        else{
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
            String s = alrnnrRunnersCatalog.get(position).getRunnerName();
            textView.setText(s);
            for(Runner r : alrnnrRunners){
                Log.d("thingy",r.toString());
                if(r.getSelectionId().compareTo(alrnnrRunnersCatalog.get(position).getSelectionId()) == 0) {
                    if (r.getStatus().compareToIgnoreCase("Active") != 0){
                        textView.setTextColor(Color.GRAY);
                        imageView.setText("Scratched");
                        imageView.setEnabled(false);
                    }
                    else if(r.getSp() == null || r.getSp().getNearPrice() == null ||r.getSp().getNearPrice() == Double.POSITIVE_INFINITY){
                        imageView.setText("-");
                    }
                    else {
                        String strSP = String.format("%.2f", r.getSp().getNearPrice());
                        imageView.setText(strSP);
                    }
                }
            }

            return rowView;
        }
    }

}
