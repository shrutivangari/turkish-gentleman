package com.shruti.turkishgentleman.ktable;

import com.shruti.turkishgentleman.clients.producer.MockDataProducer;
import com.shruti.turkishgentleman.model.StockTickerData;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KTableVsKStreams {

    public static final String STOCK_TICKER_TABLE_TOPIC = "stock-ticker-table";
    private static final String STOCK_TICKER_STREAM_TOPIC = "stock-ticker-stream";

    @Autowired
    @Qualifier("streamProperties")
    private Properties streamProperties;

    public void tableVsStream() {

        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, StockTickerData> stockTickerDataKTable = builder.table(STOCK_TICKER_TABLE_TOPIC);
        KStream<String, StockTickerData> stockTickerDataKStream = builder.stream(STOCK_TICKER_STREAM_TOPIC);

        stockTickerDataKTable.toStream()
                .print(Printed.<String, StockTickerData>toSysOut().withLabel("Stocks-KTable"));
        stockTickerDataKStream.print(Printed.<String, StockTickerData>toSysOut().withLabel("Stocks-KStream"));

        MockDataProducer.produceRandomTextData();
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamProperties);
        kafkaStreams.cleanUp();
        kafkaStreams.close();
        MockDataProducer.shutdown();
    }
}
