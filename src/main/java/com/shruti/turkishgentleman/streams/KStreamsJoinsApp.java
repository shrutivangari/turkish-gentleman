package com.shruti.turkishgentleman.streams;

import com.shruti.turkishgentleman.clients.producer.MockDataProducer;
import com.shruti.turkishgentleman.model.CorrelatedPurchase;
import com.shruti.turkishgentleman.model.Purchase;
import com.shruti.turkishgentleman.streams.joins.Joiner;
import com.shruti.turkishgentleman.utils.serde.StreamsSerdes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.shruti.turkishgentleman.utils.topics.Topics.STREAMS_JOINS;

@Component
public class KStreamsJoinsApp {

    @Autowired
    @Qualifier("streamProperties")
    private Properties streamProperties;

    public void join() {
        StreamsBuilder builder = new StreamsBuilder();

        Serde<Purchase> purchaseSerde = StreamsSerdes.PurchaseSerde();
        Serde<String> stringSerde = Serdes.String();

        KeyValueMapper<String, Purchase, KeyValue<String,Purchase>> custIdCCMasking = (k, v) -> {
            Purchase masked = Purchase.builder(v).maskCreditCard().build();
            return new KeyValue<>(masked.getCustomerId(), masked);
        };

        Predicate<String, Purchase> coffeePurchase = (key, purchase) -> purchase.getDepartment().equalsIgnoreCase("coffee");
        Predicate<String, Purchase> electronicPurchase = (key, purchase) -> purchase.getDepartment().equalsIgnoreCase("electronics");

        int COFFEE_PURCHASE = 0;
        int ELECTRONICS_PURCHASE = 1;

        KStream<String, Purchase> transactionStream = builder.stream("transactions", Consumed.with(Serdes.String(), purchaseSerde)).map(custIdCCMasking);
        KStream<String, Purchase>[] branchesStream = transactionStream.selectKey((k, v)-> v.getCustomerId()).branch(coffeePurchase, electronicPurchase);
        KStream<String, Purchase> coffeeStream = branchesStream[COFFEE_PURCHASE];
        KStream<String, Purchase> electronicsStream = branchesStream[ELECTRONICS_PURCHASE];

        ValueJoiner<Purchase, Purchase, CorrelatedPurchase> purchaseJoiner = new Joiner();
        JoinWindows twentyMinuteWindow = JoinWindows.of(60 * 1000 * 20);

        KStream<String, CorrelatedPurchase> joinedKStream = coffeeStream.join(electronicsStream,
                purchaseJoiner,
                twentyMinuteWindow,
                Joined.with(stringSerde,
                        purchaseSerde,
                        purchaseSerde));

        joinedKStream.print(Printed.<String, CorrelatedPurchase>toSysOut().withLabel("joined KStream"));

        // used only to produce data for this application, not typical usage
        MockDataProducer.produceRandomTextData(STREAMS_JOINS.topicName());
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamProperties);
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
