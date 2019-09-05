package com.shruti.turkishgentleman.run;

import com.shruti.turkishgentleman.consumer.ThreadedConsumer;
import com.shruti.turkishgentleman.mapreducelambdas.MapReduceConcepts;
import com.shruti.turkishgentleman.producer.SettingPartition;
import com.shruti.turkishgentleman.producer.SimpleProducer;
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

    public void execute() {
        System.out.println("Executing");
        mapReduceConcepts.mapDemo();
        mapReduceConcepts.reduceDemo();
        simpleProducer.produceMessages();
        settingPartition.setPartition();
        threadedConsumer.startConsuming();
    }
}
