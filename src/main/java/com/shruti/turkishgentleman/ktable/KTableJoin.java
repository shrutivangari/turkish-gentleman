package com.shruti.turkishgentleman.ktable;

import com.shruti.turkishgentleman.clients.producer.CustomDateGenerator;
import com.shruti.turkishgentleman.clients.producer.DataGenerator;
import com.shruti.turkishgentleman.clients.producer.MockDataProducer;
import com.shruti.turkishgentleman.model.StockTransaction;
import com.shruti.turkishgentleman.model.TransactionSummary;
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

import java.time.Duration;
import java.util.Properties;

import static org.apache.kafka.streams.Topology.AutoOffsetReset.EARLIEST;

public class KTableJoin {

    @Autowired
    @Qualifier("streamProperties")
    private Properties streamProperties;

    private static final String STOCK_TRANSACTIONS_TOPIC = "STOCK_TRANSACTIONS_TOPIC";

    public void joinExample() {

        Serde<String> stringSerde = Serdes.String();
        Serde<StockTransaction> transactionSerde = StreamsSerdes.StockTransactionSerde();
        Serde<TransactionSummary> transactionKeySerde = StreamsSerdes.TransactionSummarySerde();

        StreamsBuilder builder = new StreamsBuilder();
        long twentySeconds = 1000 * 20;
        long fifteenMinutes = 1000 * 60 * 15;
        long fiveSeconds = 1000 * 5;

        KTable<Windowed<TransactionSummary>, Long> customerTransactionCounts =
                builder.stream(STOCK_TRANSACTIONS_TOPIC, Consumed.with(stringSerde, transactionSerde).withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                        .groupBy((noKey, transaction) -> TransactionSummary.from(transaction),
                                Serialized.with(transactionKeySerde, transactionSerde))
                        // session window comment line below and uncomment another line below for a different window example
                        .windowedBy(SessionWindows.with(twentySeconds).until(fifteenMinutes)).count();

        //The following are examples of different windows examples

        //Tumbling window with timeout 15 minutes
        //.windowedBy(TimeWindows.of(twentySeconds).until(fifteenMinutes)).count();

        //Tumbling window with default timeout 24 hours
        //.windowedBy(TimeWindows.of(twentySeconds)).count();

        //Hopping window
        //.windowedBy(TimeWindows.of(twentySeconds).advanceBy(fiveSeconds).until(fifteenMinutes)).count();

        customerTransactionCounts.toStream().print(Printed.<Windowed<TransactionSummary>, Long>toSysOut().withLabel("Customer Transactions Counts"));

        KStream<String, TransactionSummary> countStream = customerTransactionCounts.toStream().map((window, count) -> {
            TransactionSummary transactionSummary = window.key();
            String newKey = transactionSummary.getIndustry();
            transactionSummary.setSummaryCount(count);
            return KeyValue.pair(newKey, transactionSummary);
        });

        KTable<String, String> financialNews = builder.table( "financial-news", Consumed.with(EARLIEST));


        ValueJoiner<TransactionSummary, String, String> valueJoiner = (txnct, news) ->
                String.format("%d shares purchased %s related news [%s]", txnct.getSummaryCount(), txnct.getStockTicker(), news);

        KStream<String,String> joined = countStream.leftJoin(financialNews, valueJoiner, Joined.with(stringSerde, transactionKeySerde, stringSerde));

        joined.print(Printed.<String, String>toSysOut().withLabel("Transactions and News"));



        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamProperties);
        kafkaStreams.cleanUp();

        kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
            System.out.println("had exception "+  e);
        });

        CustomDateGenerator dateGenerator = CustomDateGenerator.withTimestampsIncreasingBy(Duration.ofMillis(750));
        DataGenerator.setTimestampGenerator(dateGenerator::get);
        MockDataProducer.produceRandomTextData();

        System.out.println("Starting CountingWindowing and KTableJoins Example");
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        try {
            Thread.sleep(65000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Shutting down the CountingWindowing and KTableJoins Example Application now");
        kafkaStreams.close();
        MockDataProducer.shutdown();
    }
}
