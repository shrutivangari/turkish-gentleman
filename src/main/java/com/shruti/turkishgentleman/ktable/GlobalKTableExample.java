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
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.shruti.turkishgentleman.utils.topics.Topics.COMPANIES;
import static com.shruti.turkishgentleman.utils.topics.Topics.CLIENTS;

import java.time.Duration;
import java.util.Properties;

import static org.apache.kafka.streams.Topology.AutoOffsetReset.LATEST;

@Component
public class GlobalKTableExample {

    @Autowired
    @Qualifier("streamProperties")
    private Properties streamProperties;

    private final static String STOCK_TRANSACTIONS_TOPIC = "STOCK_TRANSACTIONS_TOPIC";

    public void global() {
        long twentySeconds = 1000 * 20;

        Serde<String> stringSerde = Serdes.String();
        Serde<StockTransaction> transactionSerde = StreamsSerdes.StockTransactionSerde();
        Serde<TransactionSummary> transactionSummarySerde = StreamsSerdes.TransactionSummarySerde();

        StreamsBuilder builder = new StreamsBuilder();

        KeyValueMapper<Windowed<TransactionSummary>, Long, KeyValue<String, TransactionSummary>> transactionMapper = (window, count) -> {
            TransactionSummary transactionSummary = window.key();
            String newKey = transactionSummary.getIndustry();
            transactionSummary.setSummaryCount(count);
            return KeyValue.pair(newKey, transactionSummary);
        };

        KStream<String, TransactionSummary> countStream =
                builder.stream(STOCK_TRANSACTIONS_TOPIC, Consumed.with(stringSerde, transactionSerde).withOffsetResetPolicy(LATEST))
                        .groupBy((noKey, transaction) -> TransactionSummary.from(transaction), Serialized.with(transactionSummarySerde, transactionSerde))
                        .windowedBy(SessionWindows.with(twentySeconds)).count()
                        .toStream().map(transactionMapper);

        GlobalKTable<String, String> publicCompanies = builder.globalTable(COMPANIES.topicName());
        GlobalKTable<String, String> clients = builder.globalTable(CLIENTS.topicName());


        countStream.leftJoin(publicCompanies, (key, txn) -> txn.getStockTicker(),TransactionSummary::withCompanyName)
                .leftJoin(clients, (key, txn) -> txn.getCustomerId(), TransactionSummary::withCustomerName)
                .print(Printed.<String, TransactionSummary>toSysOut().withLabel("Resolved Transaction Summaries"));



        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamProperties);
        kafkaStreams.cleanUp();


        kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
            System.out.println("had exception "+ e);
        });

        CustomDateGenerator dateGenerator = CustomDateGenerator.withTimestampsIncreasingBy(Duration.ofMillis(750));

        DataGenerator.setTimestampGenerator(dateGenerator::get);
//        MockDataProducer.produceRandomTextData();

        System.out.println("Starting GlobalKTableExample Example");
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        try {
            Thread.sleep(65000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Shutting down the GlobalKTableExample Example Application now");
        kafkaStreams.close();
        MockDataProducer.shutdown();
    }
}
