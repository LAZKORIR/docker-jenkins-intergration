package com.lazztechie.dockerjenkinsintergration;

import com.lazztechie.dockerjenkinsintergration.service.LambdaDemoService;
import com.lazztechie.dockerjenkinsintergration.serviceImpl.IntegrationServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DockerJenkinsIntergrationApplication {


	public static void main(String[] args) {
		SpringApplication.run(DockerJenkinsIntergrationApplication.class, args);
		IntegrationServiceImpl integrationService = new IntegrationServiceImpl();
//		integrationService.call();
//		integrationService.message();
//		integrationService.lups();
//
//		LambdaDemoService lambdaDemoService;
//		lambdaDemoService= i -> System.out.println("lambda demo  number = "+i);
//
//		lambdaDemoService.lambdaDemo(5);

		integrationService.dateTimeDemo();
	}

}
