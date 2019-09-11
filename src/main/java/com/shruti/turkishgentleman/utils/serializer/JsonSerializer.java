package com.shruti.turkishgentleman.utils.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shruti.turkishgentleman.utils.collectors.FixedSizePriorityQueue;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JsonSerializer<T> implements Serializer<T> {

    private Gson gson;

    public JsonSerializer() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(FixedSizePriorityQueue.class, new FixedSizePriorityQueueAdapter().nullSafe());
        gson = builder.create();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, T data) {
        return new byte[0];
    }

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        return new byte[0];
    }

    @Override
    public void close() {

    }
}
