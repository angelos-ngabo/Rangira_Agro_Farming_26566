package com.raf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.raf.repository")
@Slf4j
public class  RangiraAgroFarmingApplication {

public static void main(String[] args) {
log.info("==========================================");
log.info("🚀 STARTING RANGIRA AGRO FARMING SYSTEM");
log.info("==========================================");
SpringApplication.run(RangiraAgroFarmingApplication.class, args);
}

@EventListener(ApplicationReadyEvent.class)
public void onApplicationReady() {
log.info("==========================================");
log.info("✅ RANGIRA AGRO FARMING SYSTEM STARTED SUCCESSFULLY!");
log.info("✅ Backend API is ready and running");
log.info("✅ Database connection established");
log.info("✅ All services initialized");
log.info("==========================================");
}
}
