package com.shruti.turkishgentleman.partition;

import com.shruti.turkishgentleman.model.Purchase;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.springframework.stereotype.Component;

@Component
public class RewardsStreamPartitioner implements StreamPartitioner<String, Purchase> {

    @Override
    public Integer partition(String topic, String key, Purchase value, int numPartitions) {
        return value.getCustomerId().hashCode() % numPartitions;
    }
}
