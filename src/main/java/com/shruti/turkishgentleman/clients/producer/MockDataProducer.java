package com.shruti.turkishgentleman.clients.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MockDataProducer {

//    private static Producer<String, String> producer;
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static Callback callback;
    public static final int NUM_ITERATIONS = 10;

    @Autowired
    private static KafkaProducer producer;

    @Autowired
    private static MockTypeDataProducer mockTypeDataProducer;

    public static void producePurchaseData() {
        init();
        mockTypeDataProducer.producePurchaseData(NUM_ITERATIONS, producer);
    }

    public static void produceRandomTextData(final String TOPIC) {
        init();
        mockTypeDataProducer.produceRamdonData(NUM_ITERATIONS, producer, TOPIC);
    }

    public static void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        if (producer != null) {
            producer.close();
            producer = null;
        }

    }

    public static void init() {
        if(producer == null) {
            System.out.println("Initializing the producer");
            Properties properties = new Properties();
            properties.put("bootstrap.servers", "localhost:9092");
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("acks", "1");
            properties.put("retries", "3");

            producer = new KafkaProducer<>(properties);
        }

        callback = ((metadata, exception) -> {
            if(exception != null) {
                exception.printStackTrace();
            }
        });
        System.out.println("Producer initialized");
    }
}
