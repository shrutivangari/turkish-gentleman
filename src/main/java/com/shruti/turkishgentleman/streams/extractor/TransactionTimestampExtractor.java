package com.shruti.turkishgentleman.streams.extractor;

import com.shruti.turkishgentleman.model.Purchase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class TransactionTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        Purchase purchaseTransaction = (Purchase) record.value();
        return purchaseTransaction.getPurchaseDate().getTime();
    }
}
