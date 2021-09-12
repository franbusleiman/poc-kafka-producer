package com.busleiman.kafkamodel2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaModel2Application {

	public static void main(String[] args) {
		SpringApplication.run(KafkaModel2Application.class, args);

	}

}
