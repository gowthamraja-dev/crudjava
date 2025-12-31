package org.example.crudjava.api.rest.sample;

import java.time.Duration;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.crudjava.core.dto.SampleDto;
import org.example.crudjava.infrastructure.kafka.KafkaConsumerService;
import org.example.crudjava.infrastructure.kafka.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SampleController {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;

    public SampleController(
            KafkaProducerService kafkaProducerService,
            KafkaConsumerService kafkaConsumerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @GetMapping("/sample")
    public String getSample() {
        return "Hello World";
    }

    @PostMapping("/sample")
    public String postSample(@RequestBody @Valid SampleDto request) {
        return "Hello " + request.getName() + "!";
    }

    @PostMapping("/sample/kafka")
    public ResponseEntity<Void> publishToKafka(@RequestBody @Valid SampleDto request) {
        kafkaProducerService.send(kafkaProducerService.getDefaultTopic(), request.getName());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/sample/kafka")
    public ResponseEntity<String> pollFromKafka() {
        Optional<ConsumerRecord<String, String>> consumerRecord = kafkaConsumerService.poll(Duration.ofSeconds(1));
        return consumerRecord.map(recordValue -> ResponseEntity.ok(recordValue.value()))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
