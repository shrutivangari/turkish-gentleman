package com.shruti.turkishgentleman.ktable;

import com.shruti.turkishgentleman.clients.producer.MockDataProducer;
import com.shruti.turkishgentleman.model.ShareVolume;
import com.shruti.turkishgentleman.model.StockTransaction;
import com.shruti.turkishgentleman.utils.collectors.FixedSizePriorityQueue;
import com.shruti.turkishgentleman.utils.serde.StreamsSerdes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;

@Component
public class AggregationsAndReducing {

    @Autowired
    @Qualifier("streamProperties")
    private Properties streamProperties;

    public void example() {
        Serde<String> stringSerde = Serdes.String();
        Serde<StockTransaction> stockTransactionSerde = StreamsSerdes.StockTransactionSerde();
        Serde<ShareVolume> shareVolumeSerde = StreamsSerdes.ShareVolumeSerde();
        Serde<FixedSizePriorityQueue> fixedSizePriorityQueueSerde = StreamsSerdes.FixedSizePriorityQueueSerde();
        NumberFormat numberFormat = NumberFormat.getInstance();

        Comparator<ShareVolume> comparator = (sv1, sv2) -> sv2.getShares() - sv1.getShares();
        FixedSizePriorityQueue<ShareVolume> fixedQueue = new FixedSizePriorityQueue<>(comparator, 5);

        ValueMapper<FixedSizePriorityQueue, String> valueMapper = fpq -> {
            StringBuilder builder = new StringBuilder();
            Iterator<ShareVolume> iterator = fpq.iterator();
            int counter = 1;
            while(iterator.hasNext()) {
                ShareVolume stockVolume = iterator.next();
                if(stockVolume != null) {
                    builder.append(counter++)
                            .append(")")
                            .append(stockVolume.getSymbol())
                            .append(":")
                            .append(numberFormat.format(stockVolume.getShares())).append(" ");
                }
            }
            return builder.toString();
        };

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KTable<String, ShareVolume> shareVolume = streamsBuilder.stream("STOCK_TRANSACTIONS_TOPIC",
                Consumed.with(stringSerde, stockTransactionSerde)
        .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST))
                .mapValues(st -> ShareVolume.newBuilder(st).build())
                .groupBy((k,v) -> v.getSymbol(), Serialized.with(stringSerde, shareVolumeSerde))
                .reduce(ShareVolume::sum);

        shareVolume.groupBy((k, v) -> KeyValue.pair(v.getIndustry(), v), Serialized.with(stringSerde, shareVolumeSerde))
                .aggregate(() -> fixedQueue,
                        (k, v, agg) -> agg.add(v),
                        (k, v, agg) -> agg.remove(v),
                        Materialized.with(stringSerde, fixedSizePriorityQueueSerde))
                .mapValues(valueMapper)
                .toStream().peek((k, v) -> System.out.println("Stock volume by industry "+ k+ v))
                .to("stock-volume-by-company", Produced.with(stringSerde, stringSerde));


        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamProperties);
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
