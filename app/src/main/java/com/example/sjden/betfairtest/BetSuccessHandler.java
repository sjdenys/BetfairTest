package com.example.sjden.betfairtest;

/**
 * Created by sjden on 5/10/2015.
 */
public class BetSuccessHandler {

    private String strRunnerName = "";
    private Double dblSP = 0.0;
    private Double dblLiability = 0.0;

    public Double getDblLiability() {
        return dblLiability;
    }

    public void setDblLiability(Double dblLiability) {
        this.dblLiability = dblLiability;
    }

    public String getStrRunnerName() {
        return strRunnerName;
    }

    public void setStrRunnerName(String strRunnerName) {
        this.strRunnerName = strRunnerName;
    }

    public Double getDblSP() {
        return dblSP;
    }

    public void setDblSP(Double dblSP) {
        this.dblSP = dblSP;
    }

}
