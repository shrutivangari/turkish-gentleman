package com.shruti.turkishgentleman.streams.transformer;

import com.shruti.turkishgentleman.model.Purchase;
import com.shruti.turkishgentleman.model.RewardAccumulator;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Objects;

public class PurchaseRewardTransformer implements ValueTransformer<Purchase, RewardAccumulator> {

    private KeyValueStore<String, Integer> stateStore;
    private final String storeName=null;
    private ProcessorContext context;

    public PurchaseRewardTransformer(String name) {
        Objects.requireNonNull(storeName, "StoreName cannot be null");
        name = storeName;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        stateStore = (KeyValueStore) this.context.getStateStore(storeName);
    }

    @Override
    public RewardAccumulator transform(Purchase value) {
        RewardAccumulator rewardAccumulator = RewardAccumulator.builder(value).build();
        Integer accumulatedSoFar = stateStore.get(rewardAccumulator.getCustomerId());

        if(accumulatedSoFar != null) {
            rewardAccumulator.addRewardPoints(accumulatedSoFar);
        }

        stateStore.put(rewardAccumulator.getCustomerId(), rewardAccumulator.getTotalRewardPoints());
        return rewardAccumulator;
    }

    public RewardAccumulator punctuate(long timestamp) {
        return null;
    }

    @Override
    public void close() {

    }
}
