package com.shruti.turkishgentleman.streams;

import com.shruti.turkishgentleman.streams.model.Purchase;
import com.shruti.turkishgentleman.utils.serializer.JsonDeserializer;
import com.shruti.turkishgentleman.utils.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
public class StreamingApp {

    @Autowired
    @Qualifier("streamProperties")
    private Properties streamProperties;

    public void processStreams() {
        StreamsConfig streamsConfig = new StreamsConfig(streamProperties);


//        Serde<Purchase> purchaseSerde = PurchaseSerde(new JsonSerializer<>(), new JsonDeserializer<>());



    }

    private static final class PurchaseSerde extends Serdes.WrapperSerde<Purchase> {
        PurchaseSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(Purchase.class));
        }
    }



}
