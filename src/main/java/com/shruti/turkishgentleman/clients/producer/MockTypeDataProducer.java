package com.shruti.turkishgentleman.clients.producer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shruti.turkishgentleman.model.Purchase;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.shruti.turkishgentleman.utils.topics.Topics.TRANSACTIONS;

@Component
public class MockTypeDataProducer {

    @Autowired
    private static DataTypes dataTypes;

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static Callback callback;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static volatile boolean keepRunning = true;
    private static final Logger LOG = LoggerFactory.getLogger(MockTypeDataProducer.class);

    public static void produceRamdonData(int numberIterations, KafkaProducer producer, String TOPIC) {
        Runnable generateTask = () -> {
            int counter = 0;
            while(counter++ < numberIterations) {
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

    public static void producePurchaseData(int numberIterations, KafkaProducer producer) {
        Runnable generateTask = () -> {
            int counter = 0;
            while (counter++ < numberIterations  && keepRunning) {
                List<Purchase> purchases = dataTypes.generatePurchases(numberIterations);
                List<String> jsonValues = convertToJson(purchases);
                for (String value : jsonValues) {
                    ProducerRecord<String, String> record = new ProducerRecord<>(TRANSACTIONS.topicName(), null, value);
                    producer.send(record, callback);
                }
                LOG.info("Record batch sent");
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            LOG.info("Done generating purchase data");
        };
        executorService.submit(generateTask);
    }

    private static <T> List<String> convertToJson(List<T> generatedDataItems) {
        List<String> jsonList = new ArrayList<>();
        for(T generatedData: generatedDataItems) {
            jsonList.add(convertToJson(generatedData));
        }
        return jsonList;
    }

    private static <T> String convertToJson(T generatedDataItem) {
        return gson.toJson(generatedDataItem);
    }

}
