package com.lazztechie.dockerjenkinsintergration.serviceImpl;

import com.lazztechie.dockerjenkinsintergration.service.IntegrationService;

import java.util.Arrays;
import java.util.List;

public class IntegrationServiceImpl implements IntegrationService {

    @Override
    public void call() {
        System.out.println("calling my guy &&&&&&&&&");
    }

    @Override
    public void lups() {
        List<Integer> values = Arrays.asList(4,5,6,7,8);
        //print using traditional forloop
        System.out.println("traditional forloop, external loop");
        for(int i=0;i<values.size();i++){
            System.out.println(values.get(i));
        }
        //print using enhanced forloop
        System.out.println("enhanced forloop , external loop");
        for(int i : values){
            System.out.println(i);
        }

        //print using foreach
        System.out.println("foreach internal loop");
        values.forEach(i -> System.out.println(i));

        //print using stream
        System.out.println("foreach stream api");
        values.stream().forEach(i -> System.out.println(i));

        //values.forEach(i -> System.out.println(i));

    }

}
