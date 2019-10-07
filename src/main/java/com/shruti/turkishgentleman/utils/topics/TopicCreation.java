package com.shruti.turkishgentleman.utils.topics;

import com.google.common.collect.Maps;
import org.apache.kafka.clients.admin.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.shruti.turkishgentleman.utils.topics.Topics.TRANSACTIONS;

@Component
public class TopicCreation {

    int partitions=2;
    short replicationFactor=1;

    private Map<String, Object>  buildDefaultClientConfig() {
        Map<String, Object> defaultClientConfig = Maps.newHashMap();
        defaultClientConfig.put("bootstrap.servers", "localhost:9092");
        defaultClientConfig.put("client.id", "shruti-consumer");
        return defaultClientConfig;
    }


    public void createTransactionsTopic(final String topicName) {
        try(final AdminClient adminClient = KafkaAdminClient.create(buildDefaultClientConfig())) {
            try {
                final NewTopic newTopic = new NewTopic(TRANSACTIONS.topicName(), partitions, replicationFactor);
                final DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Collections.singleton(TRANSACTIONS.topicName()));
                deleteTopicsResult.values().get(topicName).get();

                final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));
                createTopicsResult.values().get(topicName).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
