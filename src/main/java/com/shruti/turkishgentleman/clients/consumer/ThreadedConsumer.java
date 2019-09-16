package com.shruti.turkishgentleman.clients.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadedConsumer {

    @Autowired
    @Qualifier("numberOfPartitions")
    private int numberOfPartitions;

    @Autowired
    @Qualifier("consumerProperties")
    private Properties consumerProperties;

    public void startConsuming() {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfPartitions);

        for(int i=0; i<numberOfPartitions;i++) {
            Runnable consumerThread = getConsumerThread(consumerProperties);
            executorService.submit(consumerThread);
        }
    }

    private Runnable getConsumerThread(Properties properties) {
        boolean doneConsuming = false;
        return () -> {
            Consumer<String, String> consumer = null;
            try {
                consumer = new KafkaConsumer<>(properties);
                consumer.subscribe(Collections.singletonList("test-topic"));

                while(!doneConsuming) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5000));
                    for(ConsumerRecord<String, String> record: records) {
                        String message = String.format("Consumed: key=%s value =%s with offset=%d partition=%d",record.key(), record.value(), record.offset(), record.partition());
                        System.out.println(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(consumer != null) {
                    consumer.close();
                }
            }
        };
    }
}
