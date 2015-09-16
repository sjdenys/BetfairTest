package com.example.sjden.betfairtest;

import com.example.sjden.betfairtest.objects.Event;
import com.example.sjden.betfairtest.objects.MarketFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by sjden on 16/09/2015.
 */
public class VenuesHandler {
//    public void sendRequestMark(){
//        strRequestType = "listMarketCatalogue";
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("filter", getMarketFilterMark());
//        params.put("maxResults", 100);
//        JSONRPCRequest jrr = new JSONRPCRequest();
//        jrr.setId("1");
//        jrr.setMethod("SportsAPING/v1.0/listMarketCatalogue");
//        jrr.setParams(params);
//        requester.sendRequest(strRequestType, jrr);
//    }
//
//    private MarketFilter getMarketFilterMark(){
//        MarketFilter mrktfltr = new MarketFilter();
//        HashSet<String> hsEventIDs = new HashSet<String>();
//        for(Event e : evntHorses){
//            hsEventIDs.add(e.getId());
//        }
//        mrktfltr.setEventIds(hsEventIDs);
//        return mrktfltr;
//    }
}
