package com.shruti.turkishgentleman.model;

import java.util.Date;
import java.util.Objects;

public class PurchasePattern {

    private String zipCode;
    private String item;
    private Date date;
    private double amount;

    private PurchasePattern(Builder builder) {
        zipCode = builder.zipCode;
        item = builder.item;
        date = builder.date;
        amount = builder.amount;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder builder(Purchase purchase) {
        return new Builder(purchase);
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getItem() {
        return item;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "PurchasePattern{" +
                "zipCode='" + zipCode + '\'' +
                ", item='" + item + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchasePattern)) return false;
        PurchasePattern that = (PurchasePattern) o;
        return Double.compare(that.getAmount(), getAmount()) == 0 &&
                Objects.equals(getZipCode(), that.getZipCode()) &&
                Objects.equals(getItem(), that.getItem()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getZipCode(), getItem(), getDate(), getAmount());
    }

    public static final class Builder {
        private String zipCode;
        private String item;
        private Date date;
        private double amount;

        private Builder() {

        }

        private Builder(Purchase purchase) {
            this.zipCode = purchase.getZipCode();
            this.item = purchase.getItemPurchased();
            this.date = purchase.getPurchaseDate();
            this.amount = purchase.getPrice()  * purchase.getQuantity();
        }

        public Builder zipCode(String val) {
            zipCode = val;
            return this;
        }

        public Builder item(String val) {
            item = val;
            return this;
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public PurchasePattern build() {
            return new PurchasePattern(this);
        }
    }
}
