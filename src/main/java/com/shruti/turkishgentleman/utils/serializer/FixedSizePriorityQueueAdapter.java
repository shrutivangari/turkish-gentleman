package com.shruti.turkishgentleman.utils.serializer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.shruti.turkishgentleman.streams.model.Purchase;
import com.shruti.turkishgentleman.utils.collectors.FixedSizePriorityQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FixedSizePriorityQueueAdapter extends TypeAdapter<FixedSizePriorityQueue<Purchase>> {

    private Gson gson = new Gson();

    @Override
    public void write(JsonWriter jsonWriter, FixedSizePriorityQueue<Purchase> purchaseFixedSizePriorityQueue) throws IOException {
        if(purchaseFixedSizePriorityQueue == null) {
            jsonWriter.nullValue();
            return;
        }

        Iterator<Purchase> iterator = purchaseFixedSizePriorityQueue.iterator();
        List<Purchase> list = new ArrayList<>();
        while(iterator.hasNext()) {
            Purchase purchase = iterator.next();
            if(purchase != null) {
                list.add(purchase);
            }
        }

        jsonWriter.beginArray();
        for(Purchase purchase: list) {
            jsonWriter.value(gson.toJson(purchase));
        }
        jsonWriter.endArray();
    }

    @Override
    public FixedSizePriorityQueue<Purchase> read(JsonReader jsonReader) throws IOException {
        List<Purchase> list = new ArrayList<>();
        jsonReader.beginArray();
        while(jsonReader.hasNext()) {
            list.add(gson.fromJson(jsonReader.nextString(), Purchase.class));
        }
        jsonReader.endArray();

        Comparator<Purchase> comparator = (c1, c2) -> c2.getQuantity() - c1.getQuantity();
        FixedSizePriorityQueue<Purchase> fixedSizePriorityQueue = new FixedSizePriorityQueue<>(comparator, 5);

        for(Purchase purchase: list) {
            fixedSizePriorityQueue.add(purchase);
        }

        return fixedSizePriorityQueue;
    }
}
