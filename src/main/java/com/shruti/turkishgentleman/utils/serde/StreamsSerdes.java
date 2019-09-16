package com.shruti.turkishgentleman.utils.serde;

import com.shruti.turkishgentleman.model.Purchase;
import com.shruti.turkishgentleman.model.PurchasePattern;
import com.shruti.turkishgentleman.model.RewardAccumulator;
import com.shruti.turkishgentleman.utils.serializer.JsonDeserializer;
import com.shruti.turkishgentleman.utils.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class StreamsSerdes {

    public static Serde<Purchase> PurchaseSerde() {
        return new PurchaseSerde();
    }

    public static Serde<PurchasePattern> PurchasePatternsSerde() {
        return new PurchasePatternsSerde();
    }

    public static Serde<RewardAccumulator> RewardAccumulatorSerde() {
        return new RewardAccumulatorSerde();
    }

    public static final class PurchaseSerde extends WrapperSerde<Purchase> {
        public PurchaseSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(Purchase.class));
        }
    }

    public static final class PurchasePatternsSerde extends WrapperSerde<PurchasePattern> {
        public PurchasePatternsSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(PurchasePattern.class));
        }
    }

    public static final class RewardAccumulatorSerde extends WrapperSerde<RewardAccumulator> {
        public RewardAccumulatorSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(RewardAccumulator.class));
        }
    }

    private static class WrapperSerde<T> implements Serde<T> {

        private JsonSerializer<T> serializer;
        private JsonDeserializer<T> deserializer;

        WrapperSerde(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {

        }

        @Override
        public void close() {

        }

        @Override
        public Serializer<T> serializer() {
            return null;
        }

        @Override
        public Deserializer<T> deserializer() {
            return null;
        }
    }
}
