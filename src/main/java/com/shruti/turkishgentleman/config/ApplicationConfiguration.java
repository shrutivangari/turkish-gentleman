package com.shruti.turkishgentleman.config;

import com.shruti.turkishgentleman.partition.PurchaseKeyPartitioner;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ApplicationConfiguration {

    private static Properties getProducerProperties() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092");
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("acks", "1");
        producerProperties.put("retries", "3");
        producerProperties.put("compression.type", "snappy");
        producerProperties.put("partitioner.class", PurchaseKeyPartitioner.class.getName());

        return producerProperties;
    }

    protected Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", StringSerializer.class.getName());
        properties.put("value.deserializer", StringSerializer.class.getName());
        properties.put("batch.size", 5);
        return properties;
    }
//
//    @Bean
//    public KafkaProducer<String, String> getKafkaProducer() {
//        return new KafkaProducer<String, String>(getProducerProperties());
//    }
//
//    @Bean
//    public KafkaConsumer<String, String> getKafkaConsumer() {
//        return new KafkaConsumer<String, String>(getConsumerProperties());
//    }

    @Bean
    public Properties producerProperties() {
        return getProducerProperties();
    }

    @Bean
    public Properties consumerProperties() {
        return getConsumerProperties();
    }
}
