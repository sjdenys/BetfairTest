package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.ClearedOrderSummary;
import com.example.sjden.betfairtest.objects.CurrentOrderSummary;
import com.example.sjden.betfairtest.objects.Event;
import com.example.sjden.betfairtest.objects.MarketCatalogue;
import com.example.sjden.betfairtest.objects.MarketFilter;
import com.example.sjden.betfairtest.objects.MarketProjection;
import com.example.sjden.betfairtest.objects.RunnerCatalog;
import com.example.sjden.betfairtest.objects.TimeRange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Hugh
 */
public class BetlistHandler implements HTTPResponseListener {
    private APINGRequester requester = new APINGRequester();
    private String strRequestType;
    private int numRaces;
    private ActivityResponseListener actrspnslstnr;
    public ArrayList<ClearedOrderSummary> clearedOrders = new ArrayList<>();
    public ArrayList<CurrentOrderSummary> currentOrders = new ArrayList<>();
    public ArrayList<MarketCatalogue> almcMarketCatalogues = new ArrayList<>();
    public ArrayList<RunnerCatalog> alrcRunnerCatalogues = new ArrayList<>();
    public ArrayList<OrderForList> aloflBetList = new ArrayList<>();

    public BetlistHandler() {
        requester.setHTTPResponseListener(BetlistHandler.this);
    }

    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public ArrayList<ClearedOrderSummary> getClearedOrders() {
        return clearedOrders;
    }

    public void setClearedOrders(ArrayList<ClearedOrderSummary> clearedOrders) {
        this.clearedOrders = clearedOrders;
    }

    public ArrayList<OrderForList> getAloflBetList() {
        return aloflBetList;
    }

    public void setAloflBetList(ArrayList<OrderForList> aloflBetList) {
        this.aloflBetList = aloflBetList;
    }

    public ArrayList<MarketCatalogue> getAlmcMarketCatalogues() {
        return almcMarketCatalogues;
    }

    public void setAlmcMarketCatalogues(ArrayList<MarketCatalogue> almcMarketCatalogues) {
        this.almcMarketCatalogues = almcMarketCatalogues;
    }

    public ArrayList<RunnerCatalog> getAlrcRunnerCatalogues() {
        return alrcRunnerCatalogues;
    }

    public void setAlmcRunnerCatalogues(ArrayList<RunnerCatalog> almcRunnerCatalogues) {
        this.alrcRunnerCatalogues = alrcRunnerCatalogues;
    }

    public void sendRequestClearedOrders(){
        strRequestType = "listClearedOrders";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("betStatus", "SETTLED");
        params.put("recordCount", 5);
        params.put("includeItemDescription", true);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listClearedOrders");
        jrr.setParams(params);
        requester.sendRequest(strRequestType, jrr);
    }

    public void sendRequestCurrentOrders(){
        strRequestType = "listCurrentOrders";
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listCurrentOrders");
        requester.sendRequest(strRequestType, jrr);
    }

    public void sendRequestMarketCatalogue(){
        strRequestType = "listMarketCatalog";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", getMarketFilter());
        HashSet<MarketProjection> mp = new HashSet<MarketProjection>();
        mp.add(MarketProjection.RUNNER_DESCRIPTION);
        mp.add(MarketProjection.MARKET_START_TIME);
        params.put("marketProjection", mp);
        params.put("maxResults", currentOrders.size() * 3);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listMarketCatalogue");
        jrr.setParams(params);
        requester.sendRequest(strRequestType, jrr);
    }

    private MarketFilter getMarketFilter(){
        MarketFilter mrktfltr = new MarketFilter();
        HashSet<String> hsMarketIDs = new HashSet<String>();
        for(CurrentOrderSummary cos : currentOrders){
            hsMarketIDs.add(cos.getMarketId());
            hsMarketIDs.add(cos.getMarketId().split("\\.")[0] + "." + Long.toString((Long.parseLong(cos.getMarketId().split("\\.")[1]) + 1)));
            hsMarketIDs.add(cos.getMarketId().split("\\.")[0] + "." + Long.toString((Long.parseLong(cos.getMarketId().split("\\.")[1]) - 1)));
        }
        mrktfltr.setMarketIds(hsMarketIDs);
        return mrktfltr;
    }

    public void populateBetList(){
        OrderForList ofl;
        for(CurrentOrderSummary cos : currentOrders){
            MarketCatalogue mcMarket = new MarketCatalogue();
            String strRunnerName = "";
            for(MarketCatalogue mc : almcMarketCatalogues){
                if(mc.getMarketId().compareTo(cos.getMarketId()) == 0){
                    mcMarket = mc;
                    if(mc.getMarketName().compareToIgnoreCase("to be placed") == 0) {
                        for(MarketCatalogue mc2 : almcMarketCatalogues){
                            if(mc2.getMarketStartTime().compareTo(mcMarket.getMarketStartTime()) == 0 && mc2.getMarketId().compareTo(mcMarket.getMarketId()) != 0){
                                mcMarket = mc2;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            for(RunnerCatalog rc : alrcRunnerCatalogues){
                if(rc.getSelectionId().compareTo(cos.getSelectionId()) == 0){
                    strRunnerName = rc.getRunnerName();
                    break;
                }
            }
            ofl = new OrderForList(strRunnerName,cos.getBspLiability(),0.0,mcMarket.getMarketName(),cos.getPlacedDate());
            aloflBetList.add(ofl);
        }
        for(ClearedOrderSummary cos : clearedOrders){
            ofl = new OrderForList(cos.getItemDescription().getRunnerDesc(),cos.getSizeSettled(), cos.getProfit(),cos.getItemDescription().getEventDesc(),cos.getPlacedDate());
            aloflBetList.add(ofl);
        }
        Log.d("thingy", aloflBetList.get(0).toString());
    }

    @Override
    public void ResponseReceived(String strRequestType, String strResponseReceived) {
        if (strResponseReceived.endsWith("Exception")) {
            actrspnslstnr.responseReceived(strResponseReceived);
        } else {
            try {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                if(strRequestType.equals("listClearedOrders")) {
                    JSONObject jObject = new JSONObject(strResponseReceived);
                    jObject = new JSONObject((jObject.getString("result")));
                    JSONArray jArray = new JSONArray(jObject.getString("clearedOrders"));
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            this.clearedOrders.add(gson.fromJson(jArray.getJSONObject(i).toString(), ClearedOrderSummary.class));
                        } catch (JSONException e) {
                            throw e;
                        }
                    }
                    populateBetList();
                    actrspnslstnr.responseReceived("");
                } else if(strRequestType.equals("listCurrentOrders")) {
                    JSONObject jObject = new JSONObject(strResponseReceived);
                    jObject = new JSONObject((jObject.getString("result")));
                    JSONArray jArray = new JSONArray(jObject.getString("currentOrders"));
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            this.currentOrders.add(gson.fromJson(jArray.getJSONObject(i).toString(), CurrentOrderSummary.class));
                        } catch (JSONException e) {
                            throw e;
                        }
                    }
                    if(this.currentOrders.size() > 0) {
                        sendRequestMarketCatalogue();
                    }
                    else{
                        sendRequestClearedOrders();
                    }
                }
                else if(strRequestType.equals("listMarketCatalog")){
                    JSONObject jObject = new JSONObject(strResponseReceived);
                    JSONArray jArray = new JSONArray((jObject.getString("result")));
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            this.almcMarketCatalogues.add(gson.fromJson(jArray.getJSONObject(i).toString(), MarketCatalogue.class));
                            JSONArray joRunners = new JSONArray(jArray.getJSONObject(i).getString("runners"));
                            for (int j = 0; j < joRunners.length(); j++) {
                                alrcRunnerCatalogues.add(gson.fromJson(joRunners.getJSONObject(j).toString(), RunnerCatalog.class));
                            }
                        } catch (JSONException e) {
                            throw e;
                        }
                    }
                    sendRequestClearedOrders();
                }
            } catch (Exception ex) {
                Log.d("thingy", ex.toString());
            }
        }
    }

    public class OrderForList{
        public OrderForList(){}
        public OrderForList(String strRunnerName,Double dblStake,Double dblProfit,String strRace,Date dtDatePlaced){
            this.strRunnerName = strRunnerName;
            this.dblStake = dblStake;
            this.dblProfit = dblProfit;
            this.strRace = strRace;
            this.dtDatePlaced = dtDatePlaced;
            if(this.dblProfit > 0){
                this.strBetStatus = "Won";
            }
            else if(this.dblProfit < 0){
                this.strBetStatus = "Lost";
            }
            else{
                this.strBetStatus = "Not yet raced";
            }
        }

        private String strRunnerName;
        private Double dblStake;
        private String strBetStatus;
        private Double dblProfit;
        private String strRace;
        private Date dtDatePlaced;

        public String getStrRunnerName() {
            return strRunnerName;
        }

        public void setStrRunnerName(String strRunnerName) {
            this.strRunnerName = strRunnerName;
        }

        public Double getDblStake() {
            return dblStake;
        }

        public void setDblStake(Double dblStake) {
            this.dblStake = dblStake;
        }

        public String getStrBetStatus() {
            return strBetStatus;
        }

        public void setStrBetStatus(String strBetStatus) {
            this.strBetStatus = strBetStatus;
        }

        public Double getDblProfit() {
            return dblProfit;
        }

        public void setDblProfit(Double dblProfit) {
            this.dblProfit = dblProfit;
        }

        public String getStrRace() {
            return strRace;
        }

        public void setStrRace(String strRace) {
            this.strRace = strRace;
        }

        public Date getDtDatePlaced() {
            return dtDatePlaced;
        }

        public void setDtDatePlaced(Date dtDatePlaced) {
            this.dtDatePlaced = dtDatePlaced;
        }

        @Override
        public String toString() {
            return "OrderForList{" +
                    "strRunnerName='" + strRunnerName + '\'' +
                    ", strBetStatus='" + strBetStatus + '\'' +
                    ", dblProfit=" + dblProfit +
                    ", strRace='" + strRace + '\'' +
                    ", dtDatePlaced=" + dtDatePlaced +
                    '}';
        }
    }
}
