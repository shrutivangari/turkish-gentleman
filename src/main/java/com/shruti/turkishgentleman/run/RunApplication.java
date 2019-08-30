package com.shruti.turkishgentleman.run;

import com.shruti.turkishgentleman.mapreducelambdas.MapReduceConcepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunApplication {

    @Autowired
    private MapReduceConcepts mapReduceConcepts;

    public void execute() {
        System.out.println("Executing");
        mapReduceConcepts.mapDemo();
        mapReduceConcepts.reduceDemo();
    }
}
