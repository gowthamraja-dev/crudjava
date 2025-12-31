package org.example.crudjava.infrastructure.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final KafkaClientProperties kafkaProperties;
    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private volatile String subscribedTopic;

    public KafkaConsumerService(KafkaClientProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        this.consumer = new KafkaConsumer<>(consumerProperties(kafkaProperties));
    }

    public Optional<ConsumerRecord<String, String>> poll(String topic, Duration timeout) {
        ensureSubscribed(topic);
        ConsumerRecords<String, String> records = consumer.poll(timeout);
        if (records.isEmpty()) {
            return Optional.empty();
        }

        ConsumerRecord<String, String> consumerRecord = records.iterator().next();
        LOGGER.debug(
                "Received message | topic={} | partition={} | offset={} | key={}",
                consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset(),
                consumerRecord.key()
        );
        return Optional.of(consumerRecord);
    }

    public Optional<ConsumerRecord<String, String>> poll(Duration timeout) {
        return poll(kafkaProperties.getDefaultTopic(), timeout);
    }

    private void ensureSubscribed(String topic) {
        if (topic == null) {
            throw new IllegalArgumentException("topic must not be null");
        }
        if (!topic.equals(subscribedTopic)) {
            consumer.subscribe(List.of(topic));
            subscribedTopic = topic;
            LOGGER.info("Subscribed Kafka consumer to topic={} with group={}", topic, kafkaProperties.getConsumerGroupId());
        }
    }

    private Map<String, Object> consumerProperties(KafkaClientProperties properties) {
        return Map.ofEntries(
                Map.entry(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers()),
                Map.entry(ConsumerConfig.GROUP_ID_CONFIG, properties.getConsumerGroupId()),
                Map.entry(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
                Map.entry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class),
                Map.entry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class),
                Map.entry(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true),
                Map.entry(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1)
        );
    }

    @PreDestroy
    public void closeConsumer() {
        if (closed.compareAndSet(false, true)) {
            LOGGER.info("Closing Kafka consumer");
            consumer.wakeup();
            consumer.close();
        }
    }
}

