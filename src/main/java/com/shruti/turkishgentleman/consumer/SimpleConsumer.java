package com.shruti.turkishgentleman.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Properties;

public class SimpleConsumer {

    @Autowired
    @Qualifier("consumerProperties")
    private Properties consumerProperties;


    public void consumeAutoCommit() {

    }
}
