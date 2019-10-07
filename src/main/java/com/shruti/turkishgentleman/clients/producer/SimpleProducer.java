package com.shruti.turkishgentleman.clients.producer;

import com.shruti.turkishgentleman.partition.PurchaseKey;
import com.shruti.turkishgentleman.utils.topics.TopicCreation;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Future;

import static com.shruti.turkishgentleman.utils.topics.Topics.TRANSACTIONS;

@Component
public class SimpleProducer {

    @Autowired
    @Qualifier("producerProperties")
    private Properties producerProperties;

    @Autowired
    private PurchaseKey purchaseKey;

    @Autowired
    private TopicCreation topicCreation;

    public void produceMessages() {

        topicCreation.createTransactionsTopic(TRANSACTIONS.topicName());

        purchaseKey.setCustomerId("12345678");
        purchaseKey.setTransactionDate(new Date());

        try(Producer<String, String> producer = new KafkaProducer<>(producerProperties)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TRANSACTIONS.topicName(), String.valueOf(purchaseKey), "{\"item\":\"book\",\"price\":10.99}");

            Callback callback = ((metadata, exception) -> {
                if(exception != null) {
                    System.out.println("Encountered exception " + exception);
                }
            });

            Future<RecordMetadata> sendFuture = producer.send(record, callback);
        }
    }
}
