package com.shruti.turkishgentleman.run;

import com.shruti.turkishgentleman.mapreducelambdas.MapReduceConcepts;
import com.shruti.turkishgentleman.producer.SimpleProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunApplication {

    @Autowired
    private MapReduceConcepts mapReduceConcepts;

    @Autowired
    private SimpleProducer simpleProducer;

    public void execute() {
        System.out.println("Executing");
        mapReduceConcepts.mapDemo();
        mapReduceConcepts.reduceDemo();
        simpleProducer.produceMessages();
    }
}
