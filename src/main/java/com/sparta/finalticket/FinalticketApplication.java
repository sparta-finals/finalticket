package com.sparta.finalticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class FinalticketApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalticketApplication.class, args);
	}

}
