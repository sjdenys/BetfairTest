package com.example.sjden.betfairtest;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by sjden on 31/08/2015.
 */
public class Login implements HTTPResponseListener {

    private APINGRequester requester;
    private APINGLoginRequester loginRequester;
    private String strRequestType;
    private String strSessionToken;

    public Login(){
        this.requester = new APINGRequester();
        this.loginRequester = new APINGLoginRequester();
        requester.setHTTPResponseListener(Login.this);
        loginRequester.setHTTPResponseListener(Login.this);
        this.strSessionToken = "";
    }

    public void SendLoginRequest(){
        strRequestType = "login";
        HashMap<String,String> hshmpURL = new HashMap<String,String>();
        hshmpURL.put("URL","https://identitysso.betfair.com/api/login");
        HashMap<String,String> hshmpParameters = new HashMap<String,String>();
        hshmpParameters.put("username", "TestAPI1");
        hshmpParameters.put("password", "change1st");
        loginRequester.sendRequest(hshmpURL, hshmpParameters);
    }

    public void SendRequest(){
        strRequestType = "events";
        HashMap<String,String> hshmpURL = new HashMap<String,String>();
        hshmpURL.put("URL","https://api-au.betfair.com/exchange/betting/json-rpc/v1");
        HashMap<String,String> hshmpHeaders = new HashMap<String,String>();
        hshmpHeaders.put("X-Authentication",this.strSessionToken);
        HashMap<String,String> hshmpParameters = new HashMap<String,String>();
        hshmpParameters.put("jsonrpc","2.0");
        hshmpParameters.put("method","SportsAPING/v1.0/");
        requester.sendRequest(hshmpURL,hshmpHeaders,hshmpParameters);
    }

    @Override
    public void ResponseReceived(String strResponseReceived) {
        if(strRequestType.compareTo("login") == 0) {
            strResponseReceived = strResponseReceived.replace("{", "").replace("}", "");
            String[] strResponseFields = strResponseReceived.split(",");
            for(String s : strResponseFields){
                if(s.split(":")[0].compareTo("\"token\"") == 0){
                    this.strSessionToken = s.split(":")[1].replace("\"", "");
                }
                else if(s.split(":")[1].compareTo("\"token\"") == 0){
                    this.strSessionToken = s.split(":")[0].replace("\"", "");
                }
            }
            Log.d("thingy", this.strSessionToken);
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
