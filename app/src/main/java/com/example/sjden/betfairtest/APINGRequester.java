package com.example.sjden.betfairtest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sjden on 28/08/2015.
 */
public class APINGRequester {

    private String strHttpResponse;

    public APINGRequester() {
        this.strHttpResponse = null;
    }

    public void sendRequest(HashMap<String,String> hshmpURL, HashMap<String,String> hshmpHeaders, HashMap<String,String> hshmpParameters){
        new localRequester().execute(hshmpURL, hshmpParameters);
    }

    public void returnResponse(String strHttpResponse){
        this.strHttpResponse = strHttpResponse;
    }

    private class localRequester extends AsyncTask<HashMap<String,String>, Void, String> {

        public String performPostCall(String requestURL, HashMap<String, String> hshmpHeaders, HashMap<String, String> postDataParams) {
            URL url;
            String response = "";
            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("X-Application","irAJdQSQfpqWMKIn");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                for(Map.Entry<String, String> entry : hshmpHeaders.entrySet()){
                    conn.setRequestProperty(entry.getKey(),entry.getValue());
                }

                Map<String,Object> params = new HashMap<String,Object>();
                params.put("filter", "");
                JSONRPC2Request reqOut = new JSONRPC2Request("SportsAPING/v1.0/listEventTypes", params, 1);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os));
                writer.write(reqOut.toString());

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    response="";
                    throw new HttpException(responseCode+"");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("thingy", e.toString());
            }

            return response;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        protected void onPreExecute(){
            //Setup is done here
        }
        @Override
        protected String doInBackground(HashMap<String,String>... params){
            return performPostCall(params[0].get("URL"), params[1], params[2]);
        }
        protected void onPostExecute(String response){
            listener.ResponseReceived(response);
        }
    }

    private HTTPResponseListener listener;

    public void setHTTPResponseListener(HTTPResponseListener listener) {
        this.listener = listener;
    }

}
