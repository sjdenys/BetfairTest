package com.example.sjden.betfairtest;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * APINGLoginRequester: sends login data to Betfair server asynchronously, receives response, then returns it via a listener
 *
 * @
 * PUBLIC FEATURES:
 * // Constructors
 *    APINGLoginRequester()
 * // Methods
 *    sendRequest(Map<String,String> mpURL, Map<String,String> mpParameters)
 *    returnResponse(String strHttpResponse)
 *    setHTTPResponseListener(HTTPResponseListener httprspnslstnr)
 *
 * MODIFIED:
 * @version 1.0, 06/09/2015, SD
 * @author Sean Denys
 */
public class APINGLoginRequester {

    private String strHttpResponse;
    private HTTPResponseListener httprspnslstnr;

    public APINGLoginRequester() {
        this.strHttpResponse = null;
    }

    /**
     * Receive a login request and pass it on to the AsyncLoginRequester to execute
     * Precondition: valid URL and parameters provided
     * @param strURL: login URL
     * @param mpParameters: username and password
     */
    public void sendRequest(String strURL, Map<String,String> mpParameters){
        new AsyncLoginRequester().execute(strURL, mpParameters);
    }

    /**
     * Records the login response so it can be returned via the listener
     * Precondition: valid login response received
     * @param strHttpResponse: login response
     */
    public void returnResponse(String strHttpResponse){
        this.strHttpResponse = strHttpResponse;
    }

    /**
     * Sets the listener that will be triggered once the login response is received
     * Precondition: valid listener provided
     * @param httprspnslstnr: the listener which will receive the login response
     */
    public void setHTTPResponseListener(HTTPResponseListener httprspnslstnr) {
        this.httprspnslstnr = httprspnslstnr;
    }

    /**
     * AsyncLoginRequester: inner class needed to asynchronously send login data
     *
     * @
     * PUBLIC FEATURES:
     * // Methods
     *    doInBackground(Object... objParams)
     *    performPostCall(String requestURL, Map<String, String> postDataParams)
     *    makePostDataString(Map<String, String> mpParams)
     *    onPostExecute(String response)
     *
     * MODIFIED:
     * @version 1.0, 06/09/2015, SD
     * @author Sean Denys
     */
    private class AsyncLoginRequester extends AsyncTask<Object, Void, String> {

        /**
         * Asynchronously executes the login request
         * Precondition: valid params provided
         * @param objParams: parameters used during the async login request
         */
        @Override
        protected String doInBackground(Object... objParams){
            return performPostCall((String)objParams[0], (Map<String,String>)objParams[1]);
        }

        /**
         * Actual login request call
         * Precondition: valid URL and request params provided
         * @param strRequestURL: login URL
         * @param mpPOSTDataParams: parameters used during the async login request
         */
        public String performPostCall(String strRequestURL, Map<String, String> mpPOSTDataParams){
            URL url;
            String strResponse = "";
            try {
                url = new URL(strRequestURL);

                HttpURLConnection httpurlcnnctn = (HttpURLConnection) url.openConnection();
                httpurlcnnctn.setReadTimeout(15000);
                httpurlcnnctn.setConnectTimeout(15000);
                httpurlcnnctn.setRequestMethod("POST");
                httpurlcnnctn.setRequestProperty("Accept", "application/json");
                httpurlcnnctn.setRequestProperty("X-Application", "irAJdQSQfpqWMKIn");
                httpurlcnnctn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream otptstrm = httpurlcnnctn.getOutputStream();
                BufferedWriter bffrdwrtr = new BufferedWriter(
                        new OutputStreamWriter(otptstrm, "UTF-8"));
                bffrdwrtr.write(makePostDataString(mpPOSTDataParams));

                bffrdwrtr.flush();
                bffrdwrtr.close();
                otptstrm.close();
                int intResponseCode=httpurlcnnctn.getResponseCode();

                if (intResponseCode == HttpsURLConnection.HTTP_OK) {
                    String strLine;
                    BufferedReader bffrdrdr = new BufferedReader(new InputStreamReader(httpurlcnnctn.getInputStream()));
                    while ((strLine=bffrdrdr.readLine()) != null) {
                        strResponse+=strLine;
                    }
                }
                else {
                    strResponse="";
                    throw new HttpException(intResponseCode + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return strResponse;
        }

        /**
         * Generated valid params for the POST call
         * Precondition: valid POST params provided
         * @param mpParams: POST params
         */
        private String makePostDataString(Map<String, String> mpParams) throws UnsupportedEncodingException {
            StringBuilder sbResult = new StringBuilder();
            boolean boolFirst = true;
            for(Map.Entry<String, String> entry : mpParams.entrySet()){
                if (boolFirst)
                    boolFirst = false;
                else
                    sbResult.append("&");

                sbResult.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sbResult.append("=");
                sbResult.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return sbResult.toString();
        }

        /**
         * After async call is completed, triggers listener with response
         * Precondition: valid login response received
         * @param strResponse: login response
         */
        protected void onPostExecute(String strResponse){
            httprspnslstnr.ResponseReceived(strResponse);
        }
    }
}
