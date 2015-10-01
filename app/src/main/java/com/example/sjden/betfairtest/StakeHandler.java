package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.MarketOnCloseOrder;
import com.example.sjden.betfairtest.objects.MarketProjection;
import com.example.sjden.betfairtest.objects.Order;
import com.example.sjden.betfairtest.objects.OrderType;
import com.example.sjden.betfairtest.objects.PlaceInstruction;
import com.example.sjden.betfairtest.objects.Side;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by sjden on 30/09/2015.
 */
public class StakeHandler implements HTTPResponseListener {

    private ActivityResponseListener actrspnslstnr;
    private APINGRequester apingrequester = new APINGRequester();
    private String strRequestType = "";
    private String strMarketID = "";
    private String strRunnerID = "";

    public StakeHandler(){
        this.apingrequester.setHTTPResponseListener(this);
    }

    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public String getStrMarketID() {
        return strMarketID;
    }

    public void setStrMarketID(String strMarketID) {
        this.strMarketID = strMarketID;
    }

    public String getStrRunnerID() {
        return strRunnerID;
    }

    public void setStrRunnerID(String strRunnerID) {
        this.strRunnerID = strRunnerID;
    }

    public void sendRequestPlaceOrder(Double dblAmount){
        Log.d("thingy", dblAmount.toString());
        strRequestType = "placeOrders";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("marketId",this.strMarketID);
        Log.d("thingy", this.strMarketID);
        ArrayList<PlaceInstruction> alpiPlaceInstructions = new ArrayList<>();
        alpiPlaceInstructions.add(getOrder(dblAmount));
        params.put("instructions",alpiPlaceInstructions);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/placeOrders");
        jrr.setParams(params);
        apingrequester.sendRequest(strRequestType, jrr);
    }

    public PlaceInstruction getOrder(Double dblAmount){
        PlaceInstruction piOrderInstructions = new PlaceInstruction();
        piOrderInstructions.setOrderType(OrderType.MARKET_ON_CLOSE);
        piOrderInstructions.setSelectionId(Long.parseLong(this.strRunnerID));
        piOrderInstructions.setSide(Side.BACK);
        MarketOnCloseOrder mocoBet = new MarketOnCloseOrder();
        mocoBet.setLiability(dblAmount);
        piOrderInstructions.setMarketOnCloseOrder(mocoBet);
        Log.d("thingy",piOrderInstructions.toString());
        return piOrderInstructions;
    }

    public void ResponseReceived(String strRequestType, String strResponseReceived) {
        if(strResponseReceived.endsWith("Exception")){
            actrspnslstnr.responseReceived(strResponseReceived);
        }
        else {
            actrspnslstnr.responseReceived("success");
        }
    }

}
