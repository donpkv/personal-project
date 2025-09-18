package com.careeros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Career OS - Enterprise-grade skill development and job readiness platform
 * 
 * Main application entry point with comprehensive feature enablement:
 * - JPA Auditing for entity tracking
 * - Caching for performance optimization
 * - Kafka messaging for event-driven architecture
 * - Async processing for non-blocking operations
 * - Scheduled tasks for background jobs
 * 
 * @author Career OS Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableKafka
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.careeros.repository")
public class CareerOSApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerOSApplication.class, args);
    }
}
