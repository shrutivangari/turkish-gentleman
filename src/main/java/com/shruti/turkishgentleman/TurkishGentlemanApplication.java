package com.shruti.turkishgentleman;

import com.shruti.turkishgentleman.run.RunApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TurkishGentlemanApplication implements CommandLineRunner {

    @Autowired
    private RunApplication runApplication;

    public static void main(String[] args) {
        SpringApplication.run(TurkishGentlemanApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.print("Hello");
        runApplication.execute();
    }
}
