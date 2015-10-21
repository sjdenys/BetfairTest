package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.Event;
import com.example.sjden.betfairtest.objects.EventType;
import com.example.sjden.betfairtest.objects.MarketBettingType;
import com.example.sjden.betfairtest.objects.MarketCatalogue;
import com.example.sjden.betfairtest.objects.MarketDescription;
import com.example.sjden.betfairtest.objects.MarketFilter;
import com.example.sjden.betfairtest.objects.TimeRange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by sjden on 8/09/2015.
 */
public class RaceTypeHandler implements HTTPResponseListener  {

    private final APINGRequester apingrequester = new APINGRequester();
    private ActivityResponseListener actrspnslstnr;
    private String strRequestType;

    public ArrayList<String> getStrHorses() {
        return strHorses;
    }

    public ArrayList<String> getStrGreyhounds() {
        return strGreyhounds;
    }

    private ArrayList<String> strHorses;
    private ArrayList<String> strGreyhounds;

    public int getIntHorseCount() {
        return intHorseCount;
    }

    private int intHorseCount;

    public int getIntGreyhoundCount() {
        return intGreyhoundCount;
    }

    private int intGreyhoundCount;

    public RaceTypeHandler() {
        this.strHorses = new ArrayList<String>();
        this.strGreyhounds = new ArrayList<String>();
        this.intHorseCount = -1;
        this.intGreyhoundCount = -1;
        this.apingrequester.setHTTPResponseListener(RaceTypeHandler.this);
    }

    /**
     * Sets the listener that will be triggered once the login response is received
     * Precondition: valid listener provided
     * @param actrspnslstnr: the listener which will receive the login response
     */
    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public void sendRequestHorseEvents(){
        strRequestType = "listHorseEvents";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", getMarketFilterHorses());
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listEvents");
        jrr.setParams(params);
        apingrequester.sendRequest(strRequestType, jrr);
    }

    public void sendRequestGreyhoundEvents(){
        strRequestType = "listGreyhoundEvents";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", getMarketFilterGreyhounds());
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listEvents");
        jrr.setParams(params);
        apingrequester.sendRequest(strRequestType, jrr);
    }

    private MarketFilter getMarketFilterHorses(){
        MarketFilter mrktfltr = new MarketFilter();
        HashSet<String> hsEventTypeIDs = new HashSet<String>();
        hsEventTypeIDs.add("7");
        TimeRange trRaceStartTimes = new TimeRange();
        GregorianCalendar gcStartsAfterTime = new GregorianCalendar();
        gcStartsAfterTime.set(Calendar.HOUR_OF_DAY, 0);
        gcStartsAfterTime.set(Calendar.MINUTE, 0);
        trRaceStartTimes.setFrom(gcStartsAfterTime.getTime());
        GregorianCalendar gcStartsBeforeTime = new GregorianCalendar();
        gcStartsBeforeTime.add(Calendar.DAY_OF_MONTH, 2);
        gcStartsBeforeTime.set(Calendar.HOUR_OF_DAY, 0);
        gcStartsBeforeTime.set(Calendar.MINUTE, 0);
        trRaceStartTimes.setTo(gcStartsBeforeTime.getTime());
        mrktfltr.setEventTypeIds(hsEventTypeIDs);
        HashSet<String> hsstrMarketType = new HashSet<String>();
        hsstrMarketType.add("PLACE");
        hsstrMarketType.add("WIN");
        mrktfltr.setMarketTypeCodes(hsstrMarketType);
        mrktfltr.setMarketStartTime(trRaceStartTimes);
        mrktfltr.setBspOnly(true);
        return mrktfltr;
    }

    private MarketFilter getMarketFilterGreyhounds(){
        MarketFilter mrktfltr = new MarketFilter();
        HashSet<String> hsEventTypeIDs = new HashSet<String>();
        hsEventTypeIDs.add("4339");
        TimeRange trRaceStartTimes = new TimeRange();
        GregorianCalendar gcStartsAfterTime = new GregorianCalendar();
        gcStartsAfterTime.set(Calendar.HOUR_OF_DAY, 0);
        gcStartsAfterTime.set(Calendar.MINUTE, 0);
        trRaceStartTimes.setFrom(gcStartsAfterTime.getTime());
        GregorianCalendar gcStartsBeforeTime = new GregorianCalendar();
        gcStartsBeforeTime.add(Calendar.DAY_OF_MONTH, 2);
        gcStartsBeforeTime.set(Calendar.HOUR_OF_DAY, 0);
        gcStartsBeforeTime.set(Calendar.MINUTE, 0);
        trRaceStartTimes.setTo(gcStartsBeforeTime.getTime());
        mrktfltr.setEventTypeIds(hsEventTypeIDs);
        HashSet<String> hsstrMarketType = new HashSet<String>();
        hsstrMarketType.add("PLACE");
        hsstrMarketType.add("WIN");
        mrktfltr.setMarketTypeCodes(hsstrMarketType);
        mrktfltr.setMarketStartTime(trRaceStartTimes);
        mrktfltr.setBspOnly(true);
        return mrktfltr;
    }

    @Override
    public void ResponseReceived(String strRequestType, Object objResponseReceived) {
        if(objResponseReceived.getClass() == Exception.class){
            actrspnslstnr.responseReceived(objResponseReceived);
        }
        else if(objResponseReceived.getClass() == String.class){
            String strResponseReceived = (String)objResponseReceived;
            switch (strRequestType) {
                case "listHorseEvents":
                    try {
                        JSONObject jObject = new JSONObject(strResponseReceived);
                        String aJsonString = jObject.getString("result");
                        JSONArray jArray = new JSONArray(aJsonString);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);
                                oneObject = new JSONObject(oneObject.getString("event"));
                                strHorses.add(oneObject.toString());
                            } catch (JSONException e) {
                                throw e;
                            }
                        }
                        intHorseCount = strHorses.size();
                        actrspnslstnr.responseReceived("");
                    }catch (Exception ex) {
                        Log.d("thingy", ex.toString());
                    }
                    break;
                case "listGreyhoundEvents":
                    try {
                        JSONObject jObject = new JSONObject(strResponseReceived);
                        String aJsonString = jObject.getString("result");
                        JSONArray jArray = new JSONArray(aJsonString);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);
                                oneObject = new JSONObject(oneObject.getString("event"));
                                strGreyhounds.add(oneObject.toString());
                            } catch (JSONException e) {
                                throw e;
                            }
                        }
                        intGreyhoundCount = strGreyhounds.size();
                        actrspnslstnr.responseReceived("");
                    } catch (Exception ex) {
                        Log.d("thingy", ex.toString());
                    }
                    break;
            }
        }
    }
}
