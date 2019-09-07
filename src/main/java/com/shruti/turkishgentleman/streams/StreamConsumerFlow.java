package com.shruti.turkishgentleman.streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;


@Component
public class StreamConsumerFlow {

//    @Autowired
//    @Qualifier("producerProperties")
//    private Properties properties;

    public void helloWorld() {
        System.out.println("Hello world for Kafka streams");
        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> simpleFirstStream = builder.stream("transactions", Consumed.with(stringSerde, stringSerde));
        KStream<String, String> upperCasedStream = simpleFirstStream.mapValues(x -> x.toUpperCase());
        upperCasedStream.to("out-topic", Produced.with(stringSerde, stringSerde));
        upperCasedStream.print(Printed .<String, String>toSysOut().withLabel("Yelling App"));
    }

    public void puttingItTogether() {
        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream("out-topic", Consumed.with(stringSerde, stringSerde))
                .mapValues(x -> x.toUpperCase())
                .to("output-topic", Produced.with(stringSerde, stringSerde));
    }
}
