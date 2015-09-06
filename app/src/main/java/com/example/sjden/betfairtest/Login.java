package com.example.sjden.betfairtest;

import android.util.Log;

import com.example.sjden.betfairtest.objects.MarketFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by sjden on 31/08/2015.
 */
public class Login implements HTTPResponseListener {

    private APINGRequester requester;
    private APINGLoginRequester loginRequester;
    private String strRequestType;

    public Login(){
        this.requester = new APINGRequester();
        this.loginRequester = new APINGLoginRequester();
        requester.setHTTPResponseListener(Login.this);
        loginRequester.setHTTPResponseListener(Login.this);
    }

    public void SendLoginRequest(){
        strRequestType = "login";
        Map<String,String> hshmpParameters = new HashMap<String,String>();
        hshmpParameters.put("username", "TestAPI1");
        hshmpParameters.put("password", "change1st");
        loginRequester.sendRequest("https://identitysso.betfair.com/api/login", hshmpParameters);
    }

    public void SendRequest(){

        strRequestType = "MarketList";
        MarketFilter mf = new MarketFilter();
        HashSet<String> hs = new HashSet<String>();
        hs.add("7");
        mf.setEventTypeIds(hs);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", mf);
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setId("1");
        jrr.setMethod("SportsAPING/v1.0/listEventTypes");
        jrr.setParams(params);
        requester.sendRequest("https://api-au.betfair.com/exchange/betting/json-rpc/v1",jrr);
    }

    @Override
    public void ResponseReceived(String strResponseReceived) {
        String strSessionToken = "";
        if(strRequestType.compareTo("login") == 0) {
            strResponseReceived = strResponseReceived.replace("{", "").replace("}", "");
            String[] strResponseFields = strResponseReceived.split(",");
            for(String s : strResponseFields){
                if(s.split(":")[0].compareTo("\"token\"") == 0){
                    strSessionToken = s.split(":")[1].replace("\"", "");
                }
                else if(s.split(":")[1].compareTo("\"token\"") == 0){
                    strSessionToken = s.split(":")[0].replace("\"", "");
                }
            }
            APINGRequester.setStrSessionKey(strSessionToken);
            SendRequest();
        }
        else{
            Log.d("thingy", strResponseReceived);
        }
    }

    public class LoginResponse {
        public String product;
        public String error;
        public String token;
        public int status;

        public LoginResponse() {
            this.product = "";
            this.error="";
            this.token = "";
            this.status = 0;
        }

    }

}
