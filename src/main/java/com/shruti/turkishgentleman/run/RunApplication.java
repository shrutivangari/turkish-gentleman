package com.shruti.turkishgentleman.run;

import com.shruti.turkishgentleman.consumer.ThreadedConsumer;
import com.shruti.turkishgentleman.mapreducelambdas.MapReduceConcepts;
import com.shruti.turkishgentleman.producer.SettingPartition;
import com.shruti.turkishgentleman.producer.SimpleProducer;
import com.shruti.turkishgentleman.streams.StreamConsumerFlow;
import com.shruti.turkishgentleman.streams.StreamingApp;
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

    public void execute() {
        System.out.println("Executing");
        kafkaStreamsEndToEnd();
    }

    private void mapReduceDemo() {
        mapReduceConcepts.mapDemo();
        mapReduceConcepts.reduceDemo();
    }

    private void producePartitionConsumeMessages() {
        simpleProducer.produceMessages();
        settingPartition.setPartition();
        //threadedConsumer.startConsuming();
    }

    private void kafkaStreamsBasics() {
        streamConsumerFlow.helloWorldStreamsDemo();
        streamConsumerFlow.helloNotDeprecated();
    }

    private void kafkaStreamsEndToEnd() {
        streamingApp.processStreams();
    }
}
