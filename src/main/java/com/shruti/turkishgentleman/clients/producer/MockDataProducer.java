package com.shruti.turkishgentleman.clients.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MockDataProducer {

//    private static Producer<String, String> producer;
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static Callback callback;
    private static String TOPIC = "Test";

    @Autowired
    private static KafkaProducer producer;

    public static void produceRandomTextData() {
        Runnable generateTask = () -> {
            init();
            int counter = 0;
            while(counter++ < 5) {
                List<String> textValues = DataGenerator.generateRandomText();

                for(String value: textValues) {
                    ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, null, value);
                    producer.send(record, callback);
                }

                try{
                    Thread.sleep(60);
                } catch(InterruptedException ex) {
                    Thread.interrupted();
                }
            }
        };
        executorService.submit(generateTask);
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

    private static void init() {
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
