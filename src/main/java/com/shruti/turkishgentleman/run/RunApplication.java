package com.shruti.turkishgentleman.run;

import com.shruti.turkishgentleman.clients.consumer.ThreadedConsumer;
import com.shruti.turkishgentleman.ktable.AggregationsAndReducing;
import com.shruti.turkishgentleman.ktable.GlobalKTableExample;
import com.shruti.turkishgentleman.ktable.KTableVsKStreams;
import com.shruti.turkishgentleman.mapreducelambdas.MapReduceConcepts;
import com.shruti.turkishgentleman.clients.producer.SettingPartition;
import com.shruti.turkishgentleman.clients.producer.SimpleProducer;
import com.shruti.turkishgentleman.streams.KStreamsJoinsApp;
import com.shruti.turkishgentleman.streams.StreamConsumerFlow;
import com.shruti.turkishgentleman.streams.StreamingApp;
import com.shruti.turkishgentleman.streams.StreamingAppAdvanced;
import com.shruti.turkishgentleman.streams.state.KafkaStreamsState;
import com.shruti.turkishgentleman.utils.topics.TopicCreation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunApplication {

    @Autowired
    private MapReduceConcepts mapReduceConcepts;

    @Autowired
    private SimpleProducer simpleProducer;

    @Autowired
    private SettingPartition settingPartition;

    @Autowired
    private ThreadedConsumer threadedConsumer;

    @Autowired
    private StreamConsumerFlow streamConsumerFlow;

    @Autowired
    private StreamingApp streamingApp;

    @Autowired
    private StreamingAppAdvanced streamingAppAdvanced;

    @Autowired
    private KStreamsJoinsApp kStreamsJoinsApp;

    @Autowired
    private KafkaStreamsState kafkaStreamsState;

    @Autowired
    private KTableVsKStreams kTableVsKStreams;

    @Autowired
    private AggregationsAndReducing aggregationsAndReducing;

    @Autowired
    private GlobalKTableExample globalKTableExample;

    public void execute() {
        System.out.println("Executing");
//        producePartitionConsumeMessages();
        kafkaStreams();
    }

    //Chapter 2 - Threaded Consumer, Custom Key Partitioner, Producer, MapReduce concepts
    private void producePartitionConsumeMessages() {
        simpleProducer.produceMessages();
        settingPartition.setPartition();
        threadedConsumer.startConsuming();
        mapReduceConcepts.mapDemo();
        mapReduceConcepts.reduceDemo();
    }

    //Chapter 3 - Kafka Streams Hello World, Process streams, Advanced app - consume
    private void kafkaStreams() {
        streamConsumerFlow.helloWorldStreamsDemo();
        streamConsumerFlow.helloNotDeprecated();
        streamingApp.processStreams();
        streamingAppAdvanced.consume();
    }

    private void kafkaStreamsFeatures() {
        kStreamsJoinsApp.join();
        kafkaStreamsState.statefulKStreams();
    }

    private void kafkaTables() {
        kTableVsKStreams.tableVsStream();
       // aggregationsAndReducing.example();
        globalKTableExample.global();
    }
}
