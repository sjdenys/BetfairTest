package com.example.sjden.betfairtest;

import com.example.sjden.betfairtest.objects.MarketCatalogue;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.example.sjden.betfairtest.objects.Event;
import com.example.sjden.betfairtest.objects.MarketFilter;
import com.example.sjden.betfairtest.objects.MarketProjection;
import com.example.sjden.betfairtest.objects.RunnerCatalog;
import com.example.sjden.betfairtest.objects.TimeRange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sjden.betfairtest.objects.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by sjden on 20/09/2015.
 */
public class RacesHandler implements HTTPResponseListener {

    private APINGRequester requester = new APINGRequester();
    private String strRequestType;
    private int numRaces;
    private ActivityResponseListener actrspnslstnr;
    ArrayList<MarketCatalogue> evntRaces = new ArrayList<>();
    ArrayList<MarketCatalogue> mcMarkets = new ArrayList<MarketCatalogue>();;
    ArrayList<String> strMarketStrings = new ArrayList<String>();
    private String strEventID;
    private HashMap<String, HashMap<String, Object>> hmMarkets = new HashMap<String, HashMap<String, Object>>();

    public HashMap<String, HashMap<String, Object>> getHmMarkets() {
        return hmMarkets;
    }

    public RacesHandler(){
        requester.setHTTPResponseListener(RacesHandler.this);
        this.numRaces = 0;
    }

    public String getStrEventID() {
        return strEventID;
    }

    public void setStrEventID(String strEventID) {
        this.strEventID = strEventID;
    }

    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public ArrayList<MarketCatalogue> getAlRaces() {
        return evntRaces;
    }

    public void sendRequestRaceEvents(){
        strRequestType = "listRaces";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", getMarketFilterRaces());
        params.put("maxResults", 100); //Change depending on how many we can fit on the screen/whatever looks good
        HashSet<MarketProjection> mp = new HashSet<MarketProjection>();
        mp.add(MarketProjection.RUNNER_DESCRIPTION);
        mp.add(MarketProjection.MARKET_START_TIME);
        params.put("marketProjection", mp);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listMarketCatalogue");
        jrr.setParams(params);
        requester.sendRequest(strRequestType, jrr);
    }

    private MarketFilter getMarketFilterRaces(){
        MarketFilter mrktfltr = new MarketFilter();
        HashSet<String> hsEventIDs = new HashSet<String>();
        hsEventIDs.add(this.strEventID); //This is the ID returned by the venue selection on the previous activity
        mrktfltr.setEventIds(hsEventIDs);
        mrktfltr.setBspOnly(true);
        HashSet<String> hsstrMarketType = new HashSet<String>();
        hsstrMarketType.add("PLACE");
        hsstrMarketType.add("WIN");
        mrktfltr.setMarketTypeCodes(hsstrMarketType);
        return mrktfltr;
    }

    @Override
    public void ResponseReceived(String strRequestType, String strResponseReceived) {
        Log.d("ResponseReceived", "Request: " + strRequestType + ". Response: " + strResponseReceived);
        if (strResponseReceived.endsWith("Exception")) {
            actrspnslstnr.responseReceived(strResponseReceived);
        } else {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
            try {
                JSONObject jObject = new JSONObject(strResponseReceived);
                JSONArray jArray = new JSONArray(jObject.getString("result"));
                for (int i = 0 ; i < jArray.length() ; i++) {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        this.mcMarkets.add(gson.fromJson(jArray.getJSONObject(i).toString(), MarketCatalogue.class));
                        HashMap<String, Object> hmTemp = new HashMap<String, Object>();
                        hmTemp.put("startTime",this.mcMarkets.get(i).getMarketStartTime());
                        hmTemp.put("runners",oneObject.get("runners").toString());
                        this.hmMarkets.put(this.mcMarkets.get(i).getMarketId(), hmTemp);
                        if(oneObject.get("marketName").toString().compareToIgnoreCase("To Be Placed") != 0){
                            this.evntRaces.add(gson.fromJson(jArray.getJSONObject(i).toString(), MarketCatalogue.class));
                            this.strMarketStrings.add(jArray.getJSONObject(i).toString());
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                }
                actrspnslstnr.responseReceived("");
            } catch (Exception ex) {
                Log.d("thingy", ex.toString());
            }
        }
    }
}
