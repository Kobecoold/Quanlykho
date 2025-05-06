package com.example.quanlikho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.quanlikho.quanlikho", "com.example.quanlikho"})
@EnableJpaRepositories(basePackages = {"com.example.quanlikho.repository"})
public class QuanlikhoApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuanlikhoApplication.class, args);
	}

}
