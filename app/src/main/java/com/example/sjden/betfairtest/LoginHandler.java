package com.example.sjden.betfairtest;

import com.example.sjden.betfairtest.objects.MarketFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by sjden on 31/08/2015.
 */
public class LoginHandler implements HTTPResponseListener {

    private APINGRequester requester;
    private APINGLoginRequester loginRequester;
    private String strRequestType;
    private ActivityResponseListener actrspnslstnr;

    public LoginHandler(){
        this.requester = new APINGRequester();
        this.loginRequester = new APINGLoginRequester();
        requester.setHTTPResponseListener(LoginHandler.this);
        loginRequester.setHTTPResponseListener(LoginHandler.this);
    }

    /**
     * Sets the listener that will be triggered once the login response is received
     * Precondition: valid listener provided
     * @param actrspnslstnr: the listener which will receive the login response
     */
    public void setActivityResponseListener(ActivityResponseListener actrspnslstnr) {
        this.actrspnslstnr = actrspnslstnr;
    }

    public void sendLoginRequest(String strUsername, String strPassword){
        strRequestType = "login";
        Map<String, String> hshmpParameters = new HashMap<String, String>();
        hshmpParameters.put("username", strUsername);
        hshmpParameters.put("password", strPassword);
        loginRequester.sendRequest("https://identitysso.betfair.com/api/login", hshmpParameters);
    }

    @Override
    public void ResponseReceived(String strRequestType, String strResponseReceived) {
        String strSessionToken = "";
        String strStatus = "";
        HashMap<String,String> hmJSON = new HashMap<>();
        strResponseReceived = strResponseReceived.replace("{", "").replace("}", "");
        String[] strResponseFields = strResponseReceived.split(",");
        if(strResponseReceived.endsWith("Exception")){
            actrspnslstnr.responseReceived(strResponseReceived);
        }
        else {
            for (String s : strResponseFields) {
                hmJSON.put(s.split(":")[0].replace("\"", ""),s.split(":")[1].replace("\"", "")) ;
            }
            if (strRequestType.compareTo("login") == 0) {
                if (hmJSON.get("status").compareTo("SUCCESS") == 0) {
                    APINGRequester.setStrSessionKey(hmJSON.get("token"));
                    actrspnslstnr.responseReceived(hmJSON.get("status"));
                } else {
                    actrspnslstnr.responseReceived(hmJSON.get("error"));
                }
            }
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
