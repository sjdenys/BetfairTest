package com.example.sjden.betfairtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sjden on 31/08/2015.
 */
public class LoginHandler implements HTTPResponseListener {

    private APINGRequester requester;
    private APINGAccountRequester accountRequester = new APINGAccountRequester();
    private APINGLoginRequester loginRequester;
    private String strRequestType;
    private ActivityResponseListener actrspnslstnr;
    private HashMap<String,String> hmJSON = new HashMap<>();

    public LoginHandler(){
        this.requester = new APINGRequester();
        this.loginRequester = new APINGLoginRequester();
        this.requester.setHTTPResponseListener(LoginHandler.this);
        this.loginRequester.setHTTPResponseListener(LoginHandler.this);
        this.accountRequester.setHTTPResponseListener(LoginHandler.this);
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

    public void requestAUSAccountFunds() {
        strRequestType = "getAccountFunds";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("wallet", "AUSTRALIAN");
        JSONRPCRequest jrr = new JSONRPCRequest();
        jrr.setMethod(Constants.ACCOUNT_APING_V1_0 + "getAccountFunds");
        jrr.setParams(params);
        jrr.setId("1");
        accountRequester.sendRequest(strRequestType, jrr);
    }

    @Override
    public void ResponseReceived(String strRequestType, Object objResponseReceived) {
        if(objResponseReceived.getClass() == Exception.class){
            actrspnslstnr.responseReceived(objResponseReceived);
        }
        else if(objResponseReceived.getClass() == String.class){
            if(this.strRequestType.compareTo("login") == 0) {
                String strLoginResponse = (String)objResponseReceived;
                strLoginResponse = strLoginResponse.replace("{", "").replace("}", "");
                String[] strResponseFields = strLoginResponse.split(",");
                for (String s : strResponseFields) {
                    hmJSON.put(s.split(":")[0].replace("\"", ""), s.split(":")[1].replace("\"", ""));
                }
                if (strRequestType.compareTo("login") == 0) {
                    if (hmJSON.get("status").compareTo("SUCCESS") == 0) {
                        APINGRequester.setStrSessionKey(hmJSON.get("token"));
                        APINGAccountRequester.setStrSessionKey(hmJSON.get("token"));
                        requestAUSAccountFunds();
                    } else {
                        actrspnslstnr.responseReceived(hmJSON.get("error"));
                    }
                }
            }
            else{
                try {
                    JSONObject jObject = new JSONObject((String)objResponseReceived);
                    JSONObject jResult = new JSONObject(jObject.getString("result"));
                    APINGAccountRequester.setDblAusBalance(jResult.getDouble("availableToBetBalance"));
                    actrspnslstnr.responseReceived(hmJSON.get("status"));
                }
                catch(Exception e){
                    Log.d("thingy",e.getMessage());
                    actrspnslstnr.responseReceived(e.getMessage());
                }
            }
        }
    }

}
