package com.example.LibDev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LibDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibDevApplication.class, args);
	}

}
