package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.MarketCatalogue;
import com.example.sjden.betfairtest.objects.MarketProjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hugh on 1/10/2015.
 */
public class WalletHandler implements HTTPResponseListener {
    private APINGAccountRequester requester = new APINGAccountRequester();
    private ActivityResponseListener actrspnslstnr;
    public String strRequestType = "";
    public Double ausBalance = 0.0;
    public Double ukBalance = 0.0;
    public String ausBalString = "";
    public String ukBalString = "";

    public WalletHandler(){
        requester.setHTTPResponseListener(WalletHandler.this);
    }

    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public void requestUKAccountFunds() {
        strRequestType = "getAccountFundsUK";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("wallet", "UK");
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setMethod(Constants.ACCOUNT_APING_V1_0 + "getAccountFunds");
        jrr.setParams(params);
        jrr.setId("1");
        requester.sendRequest(strRequestType, jrr);
    }

    public void requestAUSAccountFunds() {
        strRequestType = "getAccountFundsAus";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("wallet", "AUSTRALIAN");
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setMethod(Constants.ACCOUNT_APING_V1_0 + "getAccountFunds");
        jrr.setParams(params);
        jrr.setId("1");
        requester.sendRequest(strRequestType, jrr);
    }

    public void doTransfer(String direction, double amount) {
        strRequestType = "transferFunds";
        Map<String, Object> params = new HashMap<String, Object>();
        switch (direction) {
            case "UK>AUS":
                //do UK to AUS transfer
                params.put("from", "UK");
                params.put("to", "AUSTRALIAN");
                break;
            case "AUS>UK":
                //do AUS to UK transfer
                params.put("from", "AUSTRALIAN");
                params.put("to", "UK");
                break;
        }
        params.put("amount", amount);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setMethod(Constants.ACCOUNT_APING_V1_0 + "transferFunds");
        jrr.setParams(params);
        jrr.setId("1");
        requester.sendRequest(strRequestType, jrr);
    }

    @Override
    public void ResponseReceived(String strRequestType, String strResponseReceived) {
        Log.d("ResponseReceived", "Request: " + strRequestType + ". Response: " + strResponseReceived);
        if (strResponseReceived.endsWith("Exception")) {
            actrspnslstnr.responseReceived(strResponseReceived);
        } else {
            switch (strRequestType) {
                case "transferFunds":
                    try {
                        //update wallet values
                        requestUKAccountFunds();
                        requestAUSAccountFunds();
                        //and probably show a confirmation
                    } catch (Exception ex) {
                        Log.d("thingy", ex.toString());
                    }
                    break;
                default:
                    try {
                        //Update wallet balances
                        JSONObject jObject = new JSONObject(strResponseReceived);
                        Log.d("jsonrpc", jObject.getString("jsonrpc"));
                        JSONObject jResult = new JSONObject(jObject.getString("result"));
                        Log.d("availableToBetBalance", jResult.getString("availableToBetBalance"));
                        if (jResult.getString("wallet").equals("UK")) {
                            //update uk balance
                            ukBalance = jResult.getDouble("availableToBetBalance");
                            ukBalString = "$" + jResult.getString("availableToBetBalance");
                            Log.d("ukBalString", ukBalString);
                        } else if (jResult.getString("wallet").equals("AUSTRALIAN")) {
                            //update aus balance
                            ausBalance = jResult.getDouble("availableToBetBalance");
                            ausBalString = "$" + jResult.getString("availableToBetBalance");
                            Log.d("ausBalString", ausBalString);
                        }
                        actrspnslstnr.responseReceived(strRequestType);
                    } catch (Exception ex) {
                        Log.d("thingy", ex.toString());
                    }
                    break;
            }
        }
    }
}
