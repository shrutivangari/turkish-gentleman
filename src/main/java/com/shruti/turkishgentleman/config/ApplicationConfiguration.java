package com.shruti.turkishgentleman.config;

import com.shruti.turkishgentleman.partition.PurchaseKey;
import com.shruti.turkishgentleman.partition.PurchaseKeyPartitioner;
import com.shruti.turkishgentleman.utils.Slug;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.UUID;

@Configuration
public class ApplicationConfiguration {

    @Value("${batch.size:5}")
    private int batchSize;

    private static Properties getCustomPartitionProducerProperties() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092");
        producerProperties.put("key.serializer", PurchaseKey.class.getName());
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("acks", "1");
        producerProperties.put("retries", "3");
        producerProperties.put("compression.type", "snappy");
        producerProperties.put("partitioner.class", PurchaseKeyPartitioner.class.getName());

        return producerProperties;
    }

    private static Properties getStringProducerProperties() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092");
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("acks", "1");
        producerProperties.put("retries", "3");
        producerProperties.put("compression.type", "snappy");

        return producerProperties;
    }

    private Properties getConsumerProperties() {
        Properties properties = new Properties();
        String id = Slug.generate();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
        properties.put("batch.size", batchSize);
        properties.put("group.id", id);
        return properties;
    }

    @Bean(name = "numberOfPartitions")
    public Integer numberOfPartitions() {
        return 10;
    }

    @Bean("purchaseKeyProducerProperties")
    public Properties purchaseKeyProducerProperties() {
        return getCustomPartitionProducerProperties();
    }

    @Bean("producerProperties")
    public Properties producerProperties() {
        return getStringProducerProperties();
    }

    @Bean("consumerProperties")
    public Properties consumerProperties() {
        return getConsumerProperties();
    }

    private Properties getStreamProperties() {
        Properties streamProperties = new Properties();
        streamProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "shruti_test");
        streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return streamProperties;
    }

    @Bean("streamProperties")
    public Properties getStreamProps() {
        return getStreamProperties();
    }

    @Bean("streamsConfig")
    public StreamsConfig streamsConfiguration() {
        StreamsConfig streamsConfig = new StreamsConfig(getStreamProperties());
        return streamsConfig;
    }
}
