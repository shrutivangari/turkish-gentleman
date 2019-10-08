package com.shruti.turkishgentleman.streams.state;

import com.shruti.turkishgentleman.clients.producer.MockDataProducer;
import com.shruti.turkishgentleman.model.Purchase;
import com.shruti.turkishgentleman.model.PurchasePattern;
import com.shruti.turkishgentleman.model.RewardAccumulator;
import com.shruti.turkishgentleman.partition.RewardsStreamPartitioner;
import com.shruti.turkishgentleman.streams.transformer.PurchaseRewardTransformer;
import com.shruti.turkishgentleman.utils.serde.StreamsSerdes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.shruti.turkishgentleman.utils.topics.Topics.STREAMS_STATEFUL;

@Component
public class KafkaStreamsState {

    @Autowired
    @Qualifier("streamProperties")
    private Properties streamProperties;

    public void statefulKStreams() {
        Serde<Purchase> purchaseSerde = StreamsSerdes.PurchaseSerde();
        Serde<PurchasePattern> purchasePatternSerde = StreamsSerdes.PurchasePatternsSerde();
        Serde<RewardAccumulator> rewardAccumulatorSerde = StreamsSerdes.RewardAccumulatorSerde();
        Serde<String> stringSerde = Serdes.String();


        StreamsBuilder builder = new StreamsBuilder();
        KStream<String,Purchase> purchaseKStream = builder.stream( "transactions", Consumed.with(stringSerde, purchaseSerde))
                .mapValues(p -> Purchase.builder(p).maskCreditCard().build());

        KStream<String, PurchasePattern> patternKStream = purchaseKStream.mapValues(purchase -> PurchasePattern.builder(purchase).build());
        patternKStream.print(Printed.<String, PurchasePattern>toSysOut().withLabel("patterns"));
        patternKStream.to("patterns", Produced.with(stringSerde, purchasePatternSerde));


        //State
        String rewardsStateStoreName = "rewardsPointsStore";
        RewardsStreamPartitioner streamPartitioner = new RewardsStreamPartitioner();
        KeyValueBytesStoreSupplier storeSupplier = Stores.inMemoryKeyValueStore(rewardsStateStoreName);
        StoreBuilder<KeyValueStore<String, Integer>> storeBuilder = Stores.keyValueStoreBuilder(storeSupplier, Serdes.String(), Serdes.Integer());

        builder.addStateStore(storeBuilder);
        KStream<String, Purchase> transByCustomerStream = purchaseKStream.through( "customer_transactions", Produced.with(stringSerde, purchaseSerde, streamPartitioner));
        KStream<String, RewardAccumulator> statefulRewardAccumulator = transByCustomerStream.transformValues(() ->  new PurchaseRewardTransformer(rewardsStateStoreName),
                rewardsStateStoreName);
        statefulRewardAccumulator.print(Printed.<String, RewardAccumulator>toSysOut().withLabel("rewards"));
        statefulRewardAccumulator.to("rewards", Produced.with(stringSerde, rewardAccumulatorSerde));

        // used only to produce data for this application, not typical usage
        MockDataProducer.produceRandomTextData(STREAMS_STATEFUL.topicName());

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(),streamProperties);
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        try {
            Thread.sleep(65000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        kafkaStreams.close();
        MockDataProducer.shutdown();
    }
}
