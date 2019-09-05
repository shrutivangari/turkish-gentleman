package com.shruti.turkishgentleman.producer;

import com.shruti.turkishgentleman.partition.PurchaseKey;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Future;

@Component
public class SimpleProducer {

    @Autowired
    @Qualifier("producerProperties")
    private Properties producerProperties;

    @Autowired
    private PurchaseKey purchaseKey;

    public void produceMessages() {
//        PurchaseKey purchaseKey = new PurchaseKey();

        purchaseKey.setCustomerId("12345678");
        purchaseKey.setTransactionDate(new Date());

//        PurchaseKey key = new PurchaseKey("12345678", new Date());
        try(Producer<String, String> producer = new KafkaProducer<>(producerProperties)) {
            ProducerRecord<String, String> record = new ProducerRecord<>("transactions", String.valueOf(purchaseKey), "\"item\":\"book\",\"price\":10.99}");

            Callback callback = ((metadata, exception) -> {
                if(exception != null) {
                    System.out.println("Encountered exception " + exception);
                }
            });

            Future<RecordMetadata> sendFuture = producer.send(record, callback);
        }
    }
}