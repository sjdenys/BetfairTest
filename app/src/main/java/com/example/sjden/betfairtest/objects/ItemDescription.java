package com.example.sjden.betfairtest.objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * Created by sjden on 10/10/2015.
 */
public class ItemDescription {
    private String eventTypeDesc;
    private String eventDesc;
    private String marketDesc;
    private String marketType;
    private Date marketStartTime;
    private String runnerDesc;
    private int numberOfWinners;
    private Double eachWayDivisor;

    public Double getEachWayDivisor() {
        return eachWayDivisor;
    }

    public void setEachWayDivisor(Double eachWayDivisor) {
        this.eachWayDivisor = eachWayDivisor;
    }

    public int getNumberOfWinners() {
        return numberOfWinners;
    }

    public void setNumberOfWinners(int numberOfWinners) {
        this.numberOfWinners = numberOfWinners;
    }

    public String getRunnerDesc() {
        return runnerDesc;
    }

    public void setRunnerDesc(String runnerDesc) {
        this.runnerDesc = runnerDesc;
    }

    public Date getMarketStartTime() {
        return marketStartTime;
    }

    public void setMarketStartTime(Date marketStartTime) {
        this.marketStartTime = marketStartTime;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getMarketDesc() {
        return marketDesc;
    }

    public void setMarketDesc(String marketDesc) {
        this.marketDesc = marketDesc;
    }

    public String getEventTypeDesc() {
        return eventTypeDesc;
    }

    public void setEventTypeDesc(String eventTypeDesc) {
        this.eventTypeDesc = eventTypeDesc;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ItemDescription)) {
            return false;
        }

        if (this == o) {
            return true;
        }
        ItemDescription another = (ItemDescription)o;

        return new EqualsBuilder()
                .append(eventTypeDesc, another.eventTypeDesc)
                .append(eventDesc, another.eventDesc)
                .append(marketDesc, another.marketDesc)
                .append(marketType, another.marketType)
                .append(marketStartTime, another.marketStartTime)
                .append(runnerDesc, another.runnerDesc)
                .append(numberOfWinners, another.numberOfWinners)
                .append(eachWayDivisor, another.eachWayDivisor)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(eventTypeDesc)
                .append(eventDesc)
                .append(marketDesc)
                .append(marketType)
                .append(marketStartTime)
                .append(runnerDesc)
                .append(numberOfWinners)
                .append(eachWayDivisor)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ItemDescription{" +
                "eventTypeDesc='" + eventTypeDesc + '\'' +
                ", eventDesc='" + eventDesc + '\'' +
                ", marketDesc='" + marketDesc + '\'' +
                ", marketType='" + marketType + '\'' +
                ", marketStartTime=" + marketStartTime +
                ", runnerDesc='" + runnerDesc + '\'' +
                ", numberOfWinners=" + numberOfWinners +
                ", eachWayDivisor=" + eachWayDivisor +
                '}';
    }
}
