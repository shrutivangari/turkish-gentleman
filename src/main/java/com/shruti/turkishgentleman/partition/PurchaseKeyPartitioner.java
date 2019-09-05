package com.shruti.turkishgentleman.partition;

import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurchaseKeyPartitioner extends DefaultPartitioner {

//    @Autowired
//    private PurchaseKey purchaseKey;

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        Object newKey = null;
        if(key != null) {
            PurchaseKey purchaseKey = (PurchaseKey) key;
            newKey = purchaseKey.getCustomerId();
            keyBytes = ((String) newKey).getBytes();
        }

        return super.partition(topic, newKey, keyBytes, value, valueBytes, cluster);
    }

}
