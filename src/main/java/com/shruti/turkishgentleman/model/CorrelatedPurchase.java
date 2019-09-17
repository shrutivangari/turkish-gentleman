package com.shruti.turkishgentleman.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CorrelatedPurchase {

    private String customerId;
    private List<String> itemsPurchased;
    private double totalAmount;
    private Date firstPurchaseTime;
    private Date secondPurchaseTime;

    private CorrelatedPurchase(Builder builder) {
        customerId = builder.customerId;
        itemsPurchased = builder.itemsPurchased;
        totalAmount = builder.totalAmount;
        firstPurchaseTime = builder.firstPurchaseTime;
        secondPurchaseTime = builder.secondPurchaseTime;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<String> getItemsPurchased() {
        return itemsPurchased;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Date getFirstPurchaseTime() {
        return firstPurchaseTime;
    }

    public Date getSecondPurchaseTime() {
        return secondPurchaseTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CorrelatedPurchase)) return false;
        CorrelatedPurchase that = (CorrelatedPurchase) o;
        return Double.compare(that.getTotalAmount(), getTotalAmount()) == 0 &&
                Objects.equals(getCustomerId(), that.getCustomerId()) &&
                Objects.equals(getItemsPurchased(), that.getItemsPurchased()) &&
                Objects.equals(getFirstPurchaseTime(), that.getFirstPurchaseTime()) &&
                Objects.equals(getSecondPurchaseTime(), that.getSecondPurchaseTime());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCustomerId(), getItemsPurchased(), getTotalAmount(), getFirstPurchaseTime(), getSecondPurchaseTime());
    }

    @Override
    public String toString() {
        return "CorrelatedPurchase{" +
                "customerId='" + customerId + '\'' +
                ", itemsPurchased=" + itemsPurchased +
                ", totalAmount=" + totalAmount +
                ", firstPurchaseTime=" + firstPurchaseTime +
                ", secondPurchaseTime=" + secondPurchaseTime +
                '}';
    }

    public static final class Builder {
        private String customerId;
        private List<String> itemsPurchased;
        private double totalAmount;
        private Date firstPurchaseTime;
        private Date secondPurchaseTime;

        private Builder() {

        }

        public Builder withCustoerId(String val) {
            customerId = val;
            return this;
        }

        public Builder withItemsPurchased(List<String> val) {
            itemsPurchased = val;
            return this;
        }

        public Builder withTotalAmount(double val) {
            totalAmount = val;
            return this;
        }

        public Builder withFirstPurchaseTime(Date val) {
            firstPurchaseTime = val;
            return this;
        }

        public Builder withSecondPurchaseTime(Date val) {
            secondPurchaseTime = val;
            return this;
        }

        public CorrelatedPurchase build() {
            return new CorrelatedPurchase(this);
        }
    }

}
