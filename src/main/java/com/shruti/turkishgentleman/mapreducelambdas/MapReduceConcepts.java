package com.shruti.turkishgentleman.mapreducelambdas;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class MapReduceConcepts {

    public void mapDemo() {
        Function<LocalDate, String> addDate = (date) -> "The day of the week is " + date.getDayOfWeek();
        System.out.println(addDate.apply(LocalDate.now()));
    }

    public void reduceDemo() {
        List<Integer> numbers = Arrays.asList(1,2,3);
        int sum = numbers.stream().reduce(0, (i,j) -> i + j);
        System.out.println(sum);
    }
}
