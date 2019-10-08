package com.shruti.turkishgentleman.streams;

import com.shruti.turkishgentleman.clients.producer.MockDataProducer;
import com.shruti.turkishgentleman.model.Purchase;
import com.shruti.turkishgentleman.model.PurchasePattern;
import com.shruti.turkishgentleman.model.RewardAccumulator;
import com.shruti.turkishgentleman.utils.serde.StreamsSerdes;
import com.shruti.turkishgentleman.utils.serializer.JsonDeserializer;
import com.shruti.turkishgentleman.utils.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.shruti.turkishgentleman.utils.topics.Topics.TRANSACTIONS;

@Component
public class StreamingApp {

    @Autowired
    @Qualifier("streamProperties")
    private Properties streamProperties;

    public void processStreams() {

        //Declare Serdes
        Serde<Purchase> purchaseSerde = StreamsSerdes.PurchaseSerde();
        Serde<PurchasePattern> purchasePatternSerde = StreamsSerdes.PurchasePatternsSerde();
        Serde<RewardAccumulator> rewardAccumulatorSerde = StreamsSerdes.RewardAccumulatorSerde();
        Serde<String> stringSerde = Serdes.String();

        //Build the stream
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //KStreams
        KStream<String, Purchase> purchaseKStream = streamsBuilder.stream(TRANSACTIONS.topicName(), Consumed.with(stringSerde, purchaseSerde))
                .mapValues(p -> Purchase.builder(p).maskCreditCard().build());
        KStream<String, PurchasePattern> patternKStream = purchaseKStream.mapValues(p -> PurchasePattern.builder(p).build());
        patternKStream.print(Printed.<String, PurchasePattern>toSysOut().withLabel("patterns"));
        patternKStream.to("patterns", Produced.with(stringSerde, purchasePatternSerde));
        KStream<String, RewardAccumulator> rewardsKStream = purchaseKStream.mapValues(purchase -> RewardAccumulator.builder(purchase).build());

        rewardsKStream.print(Printed.<String, RewardAccumulator>toSysOut().withLabel("rewards"));
        rewardsKStream.to("rewards", Produced.with(stringSerde,rewardAccumulatorSerde));

        purchaseKStream.print(Printed.<String, Purchase>toSysOut().withLabel("purchases"));
        purchaseKStream.to("purchases", Produced.with(stringSerde,purchaseSerde));

        MockDataProducer.produceRandomTextData();
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(),streamProperties);
        kafkaStreams.start();
        try {
            Thread.sleep(65000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        kafkaStreams.close();
        MockDataProducer.shutdown();

    }

    private static final class PurchaseSerde extends Serdes.WrapperSerde<Purchase> {
        PurchaseSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(Purchase.class));
        }
    }



}
