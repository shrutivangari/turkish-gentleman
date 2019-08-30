package com.shruti.turkishgentleman.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SettingPartition {

    @Qualifier("numberOfPartitions")
    @Autowired
    private Integer numberOfPartitions;

    //Lock free thread safe operations on single variables
    AtomicInteger partitionIndex = new AtomicInteger(0);

    private void setPartition() {

        //Dont have to keep track of the value of the integer if it goes beyond Integer.MAX_VALUE
        int currentPartition = Math.abs(partitionIndex.getAndIncrement() % numberOfPartitions);

        ProducerRecord<String, String> record = new ProducerRecord<>("topic", currentPartition, "key", "Value");

    }
}
