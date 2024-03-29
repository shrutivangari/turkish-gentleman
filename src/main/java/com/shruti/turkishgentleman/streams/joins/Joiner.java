package com.shruti.turkishgentleman.streams.joins;

import com.shruti.turkishgentleman.model.CorrelatedPurchase;
import com.shruti.turkishgentleman.model.Purchase;
import org.apache.kafka.streams.kstream.ValueJoiner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Joiner implements ValueJoiner<Purchase, Purchase, CorrelatedPurchase> {

    @Override
    public CorrelatedPurchase apply(Purchase purchase, Purchase otherPurchase) {

        CorrelatedPurchase.Builder builder = CorrelatedPurchase.newBuilder();

        Date purchaseDate = purchase != null? purchase.getPurchaseDate() : null;
        Double price = purchase!=null? purchase.getPrice() : 0.0 ;
        String itemPurchased = purchase!=null? purchase.getItemPurchased() : null;

        Date otherPurchaseDate = otherPurchase!=null? otherPurchase.getPurchaseDate() : null;
        Double otherPrice = otherPurchase!=null? otherPurchase.getPrice(): 0.0;
        String otherItemPurchased = otherPurchase!=null? otherPurchase.getItemPurchased() : null;

        List<String> purchasedItems = new ArrayList<>();

        if(itemPurchased != null) {
            purchasedItems.add(itemPurchased);
        }

        if(otherItemPurchased != null) {
            purchasedItems.add(otherItemPurchased);
        }

        String customerId = purchase!=null? purchase.getCustomerId(): null;
        String otherCustomerId = otherPurchase!=null? otherPurchase.getCustomerId() : null;

        builder.withCustoerId(customerId!=null? customerId: otherCustomerId)
                .withFirstPurchaseTime(purchaseDate)
                .withSecondPurchaseTime(otherPurchaseDate)
                .withItemsPurchased(purchasedItems)
                .withTotalAmount(price + otherPrice);

        return builder.build();
    }
}
