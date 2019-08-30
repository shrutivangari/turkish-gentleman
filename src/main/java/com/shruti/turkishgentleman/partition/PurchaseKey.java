package com.shruti.turkishgentleman.partition;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class PurchaseKey {
    private String customerId;
    private Date transactionDate;

    public String getCustomerId() {
        return customerId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "PurchaseKey{" +
                "customerId='" + customerId + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseKey)) return false;
        PurchaseKey that = (PurchaseKey) o;
        return Objects.equals(getCustomerId(), that.getCustomerId()) &&
                Objects.equals(getTransactionDate(), that.getTransactionDate());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCustomerId(), getTransactionDate());
    }

}
