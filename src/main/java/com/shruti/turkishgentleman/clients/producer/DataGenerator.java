package com.shruti.turkishgentleman.clients.producer;

import com.github.javafaker.ChuckNorris;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {
    public static final int NUMBER_TEXT_STATEMENTS = 5;

    public static List<String> generateRandomText() {
        List<String> phrases = new ArrayList<>(NUMBER_TEXT_STATEMENTS);
        Faker faker = new Faker();

        for(int i=0;i<NUMBER_TEXT_STATEMENTS; i++) {
            ChuckNorris chuckNorris = faker.chuckNorris();
            phrases.add(chuckNorris.fact());
        }
        return phrases;
    }
}
