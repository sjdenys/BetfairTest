package com.example.sjden.betfairtest;

/**
 * Created by sjden on 31/08/2015.
 */
public interface HTTPResponseListener {
    void ResponseReceived(String strRequestType, Object objResponseReceived);
}
