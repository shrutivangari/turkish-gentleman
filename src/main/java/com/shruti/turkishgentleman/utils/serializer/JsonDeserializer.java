package com.shruti.turkishgentleman.utils.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shruti.turkishgentleman.utils.collectors.FixedSizePriorityQueue;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {

    private Gson gson;
    private Class<T> deserializedClass;
    private Type reflectiionTypeToken;

    public JsonDeserializer(Class<T> deserializedClass) {
        this.deserializedClass = deserializedClass;
        init();
    }

    public JsonDeserializer() {
    }

    private void init() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(FixedSizePriorityQueue.class, new FixedSizePriorityQueueAdapter().nullSafe());
        gson = builder.create();
    }


    @Override
    public void configure(Map<String, ?> map, boolean b) {
        if(deserializedClass == null) {
            deserializedClass = (Class<T>) map.get("serializedClass");
        }
    }

    @Override
    public void close() {

    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        if(bytes == null) {
            return null;
        }

        Type deseriazeForm = deserializedClass != null ? deserializedClass : reflectiionTypeToken;

        return gson.fromJson(new String(bytes), deseriazeForm);
    }
}
