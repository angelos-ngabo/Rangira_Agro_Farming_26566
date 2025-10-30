package com.raf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.raf.repository")
public class RangiraAgroFarmingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RangiraAgroFarmingApplication.class, args);
	}

}
