package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.EventType;
import com.example.sjden.betfairtest.objects.MarketFilter;
import com.google.gson.Gson;

import net.minidev.json.parser.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by sjden on 8/09/2015.
 */
public class RaceTypeHandler implements HTTPResponseListener  {

    private APINGRequester requester;
    private ActivityResponseListener actrspnslstnr;
    private String strRequestType;

    public RaceTypeHandler() {
        this.requester = new APINGRequester();
        requester.setHTTPResponseListener(RaceTypeHandler.this);
    }

    /**
     * Sets the listener that will be triggered once the login response is received
     * Precondition: valid listener provided
     * @param actrspnslstnr: the listener which will receive the login response
     */
    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public void SendRequest(){
        strRequestType = "MarketList";
        MarketFilter mf = new MarketFilter();
        HashSet<String> hs = new HashSet<String>();
        hs.add("7");
        hs.add("4339");
        mf.setEventTypeIds(hs);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", mf);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listEvents");
        jrr.setParams(params);
        requester.sendRequest("https://api-au.betfair.com/exchange/betting/json-rpc/v1",jrr);
    }

    @Override
    public void ResponseReceived(String strResponseReceived) {

        if(strResponseReceived.endsWith("Exception")){
            actrspnslstnr.responseReceived(strResponseReceived);
        }
        else {
            try {
                JSONObject jObject = new JSONObject(strResponseReceived);
                String aJsonString = jObject.getString("result");
                Log.d("thingy",aJsonString);
                JSONArray jArray = new JSONArray(aJsonString);
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String oneObjectsItem = oneObject.getString("event");
                        String oneObjectsItem2 = oneObject.getString("marketCount");
                        Log.d("thingy",oneObjectsItem);
                        Log.d("thingy",oneObjectsItem2);
                    } catch (JSONException e) {
                        throw e;
                    }
                }
            }catch(Exception e){
                Log.d("thingy",e.getClass().getSimpleName());
            }
        }
    }
}
