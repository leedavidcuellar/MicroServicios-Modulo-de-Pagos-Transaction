package com.Microservice.ModulosPagoTransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ModulosPagoTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModulosPagoTransactionApplication.class, args);
	}

}
