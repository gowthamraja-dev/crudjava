package org.example.crudjava;

import org.example.crudjava.infrastructure.kafka.KafkaClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(KafkaClientProperties.class)
public class CrudjavaApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrudjavaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CrudjavaApplication.class, args);
        LOGGER.info("CrudjavaApplication started");
    }
}
