package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.Event;
import com.example.sjden.betfairtest.objects.MarketCatalogue;
import com.example.sjden.betfairtest.objects.MarketFilter;
import com.example.sjden.betfairtest.objects.MarketProjection;
import com.example.sjden.betfairtest.objects.MarketSort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by sjden on 20/09/2015.
 */
public class RacesHandler implements HTTPResponseListener {

    private final APINGRequester apingrequester = new APINGRequester();
    private ActivityResponseListener actrspnslstnr;
    private String strEventId = "";
    private MarketCatalogue mrktctlg;
    private String strMarket = "";

    public RacesHandler(){
        this.apingrequester.setHTTPResponseListener(RacesHandler.this);
    }

    public String getStrEventId() {
        return this.strEventId;
    }

    public void setStrEventId(String strEventId) {
        this.strEventId = strEventId;
    }

    public String getStrMarket() {
        return strMarket;
    }

    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public void sendRequestMarkets(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", getMarketFilter());
        params.put("maxResults", 100);
        HashSet<MarketProjection> mp = new HashSet<MarketProjection>();
        mp.add(MarketProjection.RUNNER_DESCRIPTION);
        params.put("marketProjection", mp);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listMarketCatalogue");
        jrr.setParams(params);
        this.apingrequester.sendRequest("market", jrr);
    }

    private MarketFilter getMarketFilter(){
        MarketFilter mrktfltr = new MarketFilter();
        HashSet<String> hsEventIDs = new HashSet<String>();
        hsEventIDs.add(this.strEventId);
        mrktfltr.setEventIds(hsEventIDs);
        return mrktfltr;
    }

    @Override
    public void ResponseReceived(String strRequestType, String strResponseReceived) {
        try {
            JSONObject jObject = new JSONObject(strResponseReceived);
            JSONArray jArray = new JSONArray(jObject.getString("result"));
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
            for (int i = jArray.length() - 1; i >= 0 ; i--) {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    if(oneObject.get("marketName").toString().compareToIgnoreCase("To Be Placed") != 0){
                        this.strMarket = oneObject.toString();
                        break;
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
            actrspnslstnr.responseReceived("market");
        } catch (Exception ex) {
            Log.d("thingy", ex.toString());
        }
    }
}
