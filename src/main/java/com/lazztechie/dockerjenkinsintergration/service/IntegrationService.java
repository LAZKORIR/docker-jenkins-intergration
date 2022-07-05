package com.lazztechie.dockerjenkinsintergration.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public interface IntegrationService {
    void call();
    default void message(){
        System.out.println("message sent to him");
    }
    void lups();

    default void dateTimeDemo(){
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        LocalDateTime dateTime =LocalDateTime.now();

        System.out.println("date =="+date);
        System.out.println("time =="+time);
        System.out.println("dateTime =="+dateTime);
    }

    default void streamsDemo(){
       String[] names ={"laz","Angie","laban","Kim","Kaka"};
        Arrays.stream(names)//same as Streams.of(names)
                .filter(x -> x.startsWith("l"))
                .sorted()
                .forEach(System.out::println);

        int[] numbers={2,5,6};
        Arrays.stream(numbers)
                .map(x-> x * x)
                .average()
                .ifPresent(System.out::println);

        List<String> people = Arrays.asList("LAzz","ANGIE","LABAN","KABUlio");
        people
                .stream()
                .map(String::toLowerCase)
                .filter(x -> x.startsWith("l"))
                .sorted()
                .forEach(System.out::println);

    }
}
