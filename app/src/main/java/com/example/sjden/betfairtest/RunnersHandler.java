package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.Event;
import com.example.sjden.betfairtest.objects.MarketFilter;
import com.example.sjden.betfairtest.objects.PriceData;
import com.example.sjden.betfairtest.objects.PriceProjection;
import com.example.sjden.betfairtest.objects.Runner;
import com.example.sjden.betfairtest.objects.RunnerCatalog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Sean on 24/09/2015.
 */
public class RunnersHandler implements HTTPResponseListener{

    private final APINGRequester apingrequester = new APINGRequester();
    private ActivityResponseListener actrspnslstnr;
    private final ArrayList<RunnerCatalog> alrcRunnerCatalog = new ArrayList<>();

    private final ArrayList<Runner> alrnnrRunners = new ArrayList<>();
    private final ArrayList<Runner> alrnnrPlaceRunners = new ArrayList<>();
    private String strRequestType = "";
    private String strWinMarketId = "";
    private String strPlaceMarketId = "";
    private String strMarketId = "";

    public RunnersHandler(){
        this.apingrequester.setHTTPResponseListener(RunnersHandler.this);
    }


    public String getStrMarketId() {
        return strMarketId;
    }

    public void setStrMarketId(String strMarketId) {
        this.strMarketId = strMarketId;
    }

    public String getStrWinMarketId() {
        return this.strWinMarketId;
    }

    public void setStrWinMarketId(String strMarketId) {
        this.strWinMarketId = strMarketId;
    }

    public String getStrPlaceMarketId() {
        return this.strPlaceMarketId;
    }

    public void setStrPlaceMarketId(String strPlaceMarketId) {
        this.strPlaceMarketId = strPlaceMarketId;
    }

    public ArrayList<RunnerCatalog> getAlrcRunnerCatalog() {
        return alrcRunnerCatalog;
    }

    public ArrayList<Runner> getAlrnnrRunners() {
        return alrnnrRunners;
    }


    public ArrayList<Runner> getAlrnnrPlaceRunners() {
        return alrnnrPlaceRunners;
    }


    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public void sendRequestMarketBook(){
        this.strRequestType = "listMarketBook";
        Map<String, Object> params = new HashMap<String, Object>();
        ArrayList<String> alstrMarketIDs = new ArrayList<String>();
        alstrMarketIDs.add(this.strWinMarketId);
        alstrMarketIDs.add(this.strPlaceMarketId);
        params.put("marketIds", alstrMarketIDs);
        PriceProjection pp = new PriceProjection();
        HashSet<PriceData> pd = new HashSet<>();
        pd.add(PriceData.SP_AVAILABLE);
        pp.setPriceData(pd);
        params.put("priceProjection",pp);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listMarketBook");
        jrr.setParams(params);
        this.apingrequester.sendRequest(strRequestType, jrr);
    }

    @Override
    public void ResponseReceived(String strRequestType, String strResponseReceived) {
        if(strResponseReceived.endsWith("Exception")){
            this.actrspnslstnr.responseReceived(strResponseReceived);
        }
        else {
            try {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                JSONObject jObject = new JSONObject(strResponseReceived);
                JSONArray jArray = new JSONArray(jObject.getString("result"));
                JSONArray jaRunners;
                JSONObject joMarket;
                for (int i = 0; i < jArray.length(); i++) {
                    joMarket = jArray.getJSONObject(i);
                    String strMarketId = joMarket.getString("marketId");
                    jaRunners = new JSONArray(joMarket.getString("runners"));
                    try {
                        if(strMarketId.compareTo(strWinMarketId) == 0){
                            for (int j = 0; j < jaRunners.length(); j++) {
                                this.alrnnrRunners.add(gson.fromJson(jaRunners.getJSONObject(j).toString(), Runner.class));
                            }
                        }
                        else{
                            for (int j = 0; j < jaRunners.length(); j++) {
                                this.alrnnrPlaceRunners.add(gson.fromJson(jaRunners.getJSONObject(j).toString(), Runner.class));
                            }                        }
                    } catch (JSONException e) {
                        throw e;
                    }
                }
                this.actrspnslstnr.responseReceived("listMarketBook");
            }catch (Exception ex) {
                Log.d("thingy", ex.toString());
            }
        }
    }

}
