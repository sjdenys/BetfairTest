package com.example.sjden.betfairtest;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * APINGRequester: sends requests to Betfair API asynchronously, receives response, then returns it via a listener
 *
 * @
 * PUBLIC FEATURES:
 * // Constructors
 *    APINGRequester()
 * // Methods
 *    sendRequest(String strURL mpURL, jrrParams)
 *    returnResponse(String strHttpResponse)
 *    getStrHttpResponse()
 *    setStrSessionKey(String strSessionKey)
 *    setHTTPResponseListener(HTTPResponseListener httprspnslstnr)
 *
 * MODIFIED:
 * @version 1.0, 06/09/2015, SD
 * @author Sean Denys
 */
public class APINGRequester {

    private String strHttpResponse;
    private static String strSessionKey = "";
    private static String strAppKey = "";
    private HTTPResponseListener listener;


    public static String getStrAppKey() {
        return strAppKey;
    }

    public static void setStrAppKey(String strAppKey) {
        APINGRequester.strAppKey = strAppKey;
    }

    public APINGRequester() {
        this.strHttpResponse = null;
    }

    public static String getStrHttpResponse(){
        return APINGRequester.getStrHttpResponse();
    }

    public static void setStrSessionKey(String strSessionKey) {
        APINGRequester.strSessionKey = strSessionKey;
    }

    /**
     * Receive a JSON-RPC request and pass it on to the AsyncRequester to execute
     * Precondition: valid URL and JSON-RPC request provided
     * @param jrrParams: JSON-RPC request
     */
    public void sendRequest(String strRequestType, JSONRPCRequest jrrParams){
        new AsyncRequester().execute(strRequestType,jrrParams);
    }

    /**
     * Records the API response so it can be returned via the listener
     * Precondition: valid API response received
     * @param strHttpResponse: API response
     */
    public void returnResponse(String strHttpResponse){
        this.strHttpResponse = strHttpResponse;
    }

    /**
     * Sets the listener that will be triggered once the API response is received
     * Precondition: valid listener provided
     * @param httprspnslstnr: the listener which will receive the API response
     */
    public void setHTTPResponseListener(HTTPResponseListener httprspnslstnr) {
        this.listener = httprspnslstnr;
    }

    /**
     * AsyncRequester: inner class needed to asynchronously send API request data
     *
     * @
     * PUBLIC FEATURES:
     * // Methods
     *    doInBackground(Object... objParams)
     *    performPostCall(String requestURL, JSONRPCRequest jrrAPIRequest)
     *    onPostExecute(String response)
     *
     * MODIFIED:
     * @version 1.0, 06/09/2015, SD
     * @author Sean Denys
     */
    private class AsyncRequester extends AsyncTask<Object, Void, ArrayList<Object>> {
        /**
         * Asynchronously executes the API request
         * Precondition: valid params provided
         * @param objParams: parameters used during the async API request
         */
        @Override
        protected ArrayList<Object> doInBackground(Object... objParams){
            return performPostCall((String)objParams[0], (JSONRPCRequest)objParams[1]);
        }

        /**
         * Actual API request call
         * Precondition: valid URL and request params provided
         * @param jrrAPIRequest: parameters used during the async API request
         */
        public ArrayList<Object> performPostCall(String strRequestType, JSONRPCRequest jrrAPIRequest) {
            URL url;
            Object objResponse = null;
            String strResponse = "";
            try {
                url = new URL(Constants.URL);

                HttpURLConnection httpurlcnnctn = (HttpURLConnection) url.openConnection();
                httpurlcnnctn.setReadTimeout(15000);
                httpurlcnnctn.setConnectTimeout(15000);
                httpurlcnnctn.setRequestMethod("POST");
                httpurlcnnctn.setRequestProperty("X-Application",  APINGRequester.strAppKey);
                httpurlcnnctn.setRequestProperty("Content-Type", "application/json");
                httpurlcnnctn.setRequestProperty("X-Authentication", APINGRequester.strSessionKey);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                String strRequestString = gson.toJson(jrrAPIRequest);
                Log.d("thingy",strRequestString);
                OutputStream otptstrm = httpurlcnnctn.getOutputStream();
                BufferedWriter bffrdwrtr = new BufferedWriter(new OutputStreamWriter(otptstrm));
                bffrdwrtr.write(strRequestString);
                bffrdwrtr.flush();
                bffrdwrtr.close();
                otptstrm.close();
                int intResponseCode=httpurlcnnctn.getResponseCode();

                if (intResponseCode == HttpsURLConnection.HTTP_OK) {
                    String strLine;
                    BufferedReader bffrdrdr = new BufferedReader(new InputStreamReader(httpurlcnnctn.getInputStream()));
                    while ((strLine=bffrdrdr.readLine()) != null) {
                        strResponse += strLine;
                    }
                }
                else {
                    throw new HttpException(intResponseCode + "");
                }
                objResponse = strResponse;
            } catch (Exception e) {
                Log.d("thingy",e.getMessage());
                objResponse = e;
            }
            finally {
                ArrayList<Object> alResponse = new ArrayList<Object>();
                alResponse.add(strRequestType);
                alResponse.add(objResponse);
                return alResponse;
            }
        }

        /**
         * After async call is completed, triggers listener with response
         * Precondition: valid API response received
         * @param objResponse: API response
         */
        protected void onPostExecute(ArrayList<Object> objResponse){
            Log.d("thingy",(String)objResponse.get(1));
            listener.ResponseReceived((String)objResponse.get(0), objResponse.get(1));
        }
    }

}
