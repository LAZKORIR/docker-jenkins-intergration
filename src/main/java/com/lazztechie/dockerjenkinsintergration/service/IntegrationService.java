package com.lazztechie.dockerjenkinsintergration.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
}
