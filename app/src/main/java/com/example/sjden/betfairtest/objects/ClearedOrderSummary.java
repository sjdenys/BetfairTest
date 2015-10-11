package com.example.sjden.betfairtest.objects;

import java.util.Date;

/**
 * Created by sjden on 10/10/2015.
 */
public class ClearedOrderSummary {
    private String eventTypeId;
    private String eventId;
    private String marketId;
    private Long selectionId;
    private Double handicap;
    private String betId;
    private Date placedDate;
    private String persistenceType;
    private String orderType;
    private String side;
    private ItemDescription itemDescription;
    private String betOutcome;
    private Double price;
    private Date settledDate;
    private Date lastMatchedDate;
    private int betCount;
    private Double commission;
    private Double priceMatched;
    private Boolean priceReduced;
    private Double sizeSettled;
    private Double profit;
    private Double sizeCancelled;

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public Long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(Long selectionId) {
        this.selectionId = selectionId;
    }

    public Double getHandicap() {
        return handicap;
    }

    public void setHandicap(Double handicap) {
        this.handicap = handicap;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        this.placedDate = placedDate;
    }

    public String getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(String persistenceType) {
        this.persistenceType = persistenceType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public ItemDescription getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(ItemDescription itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getBetOutcome() {
        return betOutcome;
    }

    public void setBetOutcome(String betOutcome) {
        this.betOutcome = betOutcome;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(Date settledDate) {
        this.settledDate = settledDate;
    }

    public Date getLastMatchedDate() {
        return lastMatchedDate;
    }

    public void setLastMatchedDate(Date lastMatchedDate) {
        this.lastMatchedDate = lastMatchedDate;
    }

    public int getBetCount() {
        return betCount;
    }

    public void setBetCount(int betCount) {
        this.betCount = betCount;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getPriceMatched() {
        return priceMatched;
    }

    public void setPriceMatched(Double priceMatched) {
        this.priceMatched = priceMatched;
    }

    public Boolean getPriceReduced() {
        return priceReduced;
    }

    public void setPriceReduced(Boolean priceReduced) {
        this.priceReduced = priceReduced;
    }

    public Double getSizeSettled() {
        return sizeSettled;
    }

    public void setSizeSettled(Double sizeSettled) {
        this.sizeSettled = sizeSettled;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Double getSizeCancelled() {
        return sizeCancelled;
    }

    public void setSizeCancelled(Double sizeCancelled) {
        this.sizeCancelled = sizeCancelled;
    }

    @Override
    public String toString() {
        return "ClearedOrderSummary{" +
                "eventTypeId='" + eventTypeId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", marketId='" + marketId + '\'' +
                ", selectionId=" + selectionId +
                ", handicap=" + handicap +
                ", betId='" + betId + '\'' +
                ", placedDate=" + placedDate +
                ", persistenceType='" + persistenceType + '\'' +
                ", orderType='" + orderType + '\'' +
                ", side='" + side + '\'' +
                ", itemDescription=" + itemDescription +
                ", betOutcome='" + betOutcome + '\'' +
                ", price=" + price +
                ", settledDate=" + settledDate +
                ", lastMatchedDate=" + lastMatchedDate +
                ", betCount=" + betCount +
                ", commission=" + commission +
                ", priceMatched=" + priceMatched +
                ", priceReduced=" + priceReduced +
                ", sizeSettled=" + sizeSettled +
                ", profit=" + profit +
                ", sizeCancelled=" + sizeCancelled +
                '}';
    }

}
