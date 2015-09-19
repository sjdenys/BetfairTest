package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.Event;
import com.example.sjden.betfairtest.objects.MarketFilter;
import com.example.sjden.betfairtest.objects.MarketProjection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by sjden on 16/09/2015.
 */
public class VenuesHandler implements HTTPResponseListener {

    private APINGRequester apingrequester = new APINGRequester();
    private ActivityResponseListener actrspnslstnr;
    private ArrayList<Event> alevntEvents = new ArrayList<>();

    private ArrayList<Event> alevntTodayEvents = new ArrayList<>();
    private ArrayList<Event> alevntTomorrowEvents = new ArrayList<>();
    private String strRequestType = "";

    public VenuesHandler(){
        apingrequester.setHTTPResponseListener(VenuesHandler.this);
    }

    /**
     * Sets the listener that will be triggered once the login response is received
     * Precondition: valid listener provided
     * @param actrspnslstnr: the listener which will receive the login response
     */
    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public ArrayList<Event> getAlevntEvents() {
        return alevntEvents;
    }

    public void setAlevntEvents(ArrayList<Event> alevntEvents) {
        this.alevntEvents = alevntEvents;
    }

    public ArrayList<Event> getAlevntTodayEvents() {
        return alevntTodayEvents;
    }

    public ArrayList<Event> getAlevntTomorrowEvents() {
        return alevntTomorrowEvents;
    }


    public void sortAndDivideEvents(){
        GregorianCalendar gcToday = new GregorianCalendar();
        for(Event e : alevntEvents){
            if(e.getOpenDate().getDay() == gcToday.getTime().getDay()){
                alevntTodayEvents.add(e);
            }
            else{
                alevntTomorrowEvents.add(e);
            }
        }
        ArrayList<Event> alevntTemp = new ArrayList<Event>(alevntTodayEvents);
        for(int i = 0 ; i < alevntTodayEvents.size() ; i++){
            if(alevntTodayEvents.get(i).getCountryCode().compareTo("NZ") == 0){
                alevntTemp.remove(alevntTodayEvents.get(i));
                alevntTemp.add(alevntTodayEvents.get(i));
            }
        }
        alevntTodayEvents = alevntTemp;
        alevntTemp = new ArrayList<Event>(alevntTomorrowEvents);
        for(int i = 0 ; i < alevntTomorrowEvents.size() ; i++){
            if(alevntTomorrowEvents.get(i).getCountryCode().compareTo("NZ") == 0){
                alevntTemp.remove(alevntTomorrowEvents.get(i));
                alevntTemp.add(alevntTomorrowEvents.get(i));
            }
        }
        alevntTomorrowEvents = alevntTemp;
    }

    public void sendRequestThoroughbredMarkets(){
        strRequestType = "listMarketCatalogueThoroughbreds";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", getMarketFilter());
        HashSet<Object> mp = new HashSet<Object>();
        mp.add(MarketProjection.EVENT);
        //mp.add(MarketProjection.RUNNER_DESCRIPTION);
        params.put("marketProjection",mp);
        params.put("maxResults", 1000);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listMarketCatalogue");
        jrr.setParams(params);
        apingrequester.sendRequest(strRequestType, jrr);
    }

    public void sendRequestHarnessMarkets(){
        strRequestType = "listMarketCatalogueHarness";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", getMarketFilter());
        HashSet<Object> mp = new HashSet<Object>();
        mp.add(MarketProjection.EVENT);
       // mp.add(MarketProjection.RUNNER_DESCRIPTION);
        params.put("marketProjection",mp);
        params.put("maxResults", 1000);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listMarketCatalogue");
        jrr.setParams(params);
        apingrequester.sendRequest(strRequestType, jrr);
    }

    private MarketFilter getMarketFilter(){
        MarketFilter mrktfltr = new MarketFilter();
        HashSet<String> hsEventIDs = new HashSet<String>();
        for(Event e : alevntEvents){
            hsEventIDs.add(e.getId());
        }
        mrktfltr.setEventIds(hsEventIDs);
        return mrktfltr;
    }

    @Override
    public void ResponseReceived(String strRequestType, String strResponseReceived) {
        if(strResponseReceived.endsWith("Exception")){
            actrspnslstnr.responseReceived(strResponseReceived);
        }
        else {
            switch (strRequestType) {
                case "listMarketCatalogueThoroughbreds":
                    Log.d("thingy",Integer.toString(alevntEvents.size()));
                    try {
                        JSONObject jObject = new JSONObject(strResponseReceived);
                        JSONArray jArray = new JSONArray(jObject.getString("result"));
                        ArrayList<Event> alevntEventsCopy = new ArrayList<Event>(alevntEvents);
                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);
                                JSONObject jsonobjEvent = new JSONObject(oneObject.getString("event"));
                                String strEvent = (String)jsonobjEvent.get("id");
                                if(oneObject.toString().contains("Pace") || oneObject.toString().contains("Trot")){
                                    for(Event e : alevntEvents){
                                        if(e.getId().compareTo(strEvent) == 0){
                                            alevntEventsCopy.remove(e);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                throw e;
                            }
                        }
                        alevntEvents = alevntEventsCopy;
                        sortAndDivideEvents();
                        actrspnslstnr.responseReceived("");
                    }catch (Exception ex) {
                        Log.d("thingy", ex.toString());
                    }
                    break;
                case "listMarketCatalogueHarness":
                    try {
                        JSONObject jObject = new JSONObject(strResponseReceived);
                        JSONArray jArray = new JSONArray(jObject.getString("result"));
                        ArrayList<Event> alevntHarnessEvents = new ArrayList<Event>();
                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);
                                JSONObject jsonobjEvent = new JSONObject(oneObject.getString("event"));
                                String strEvent = (String)jsonobjEvent.get("id");
                                if(oneObject.toString().contains("Pace") || oneObject.toString().contains("Trot")){
                                    for(Event e : alevntEvents){
                                        if(e.getId().compareTo(strEvent) == 0){
                                            alevntHarnessEvents.add(e);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                throw e;
                            }
                        }
                        Set setItems = new LinkedHashSet(alevntHarnessEvents);
                        alevntEvents.clear();
                        alevntEvents.addAll(setItems);
                        sortAndDivideEvents();
                        actrspnslstnr.responseReceived("");
                    }catch (Exception ex) {
                        Log.d("thingy", ex.toString());
                    }
                    break;
            }
        }
    }
}
