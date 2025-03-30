package com.celikhakan.messaging.messaging_service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MessagingServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(MessagingServiceApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(MessagingServiceApplication.class, args);
		logger.info("🚀 Hello Elastic from Spring Boot - Hakan");
	}

}
