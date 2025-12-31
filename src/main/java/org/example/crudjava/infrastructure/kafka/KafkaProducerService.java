package org.example.crudjava.infrastructure.kafka;

import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.crudjava.core.util.UniqueIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaClientProperties kafkaProperties;
    private final KafkaProducer<String, String> producer;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public KafkaProducerService(KafkaClientProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        this.producer = new KafkaProducer<>(producerProperties(kafkaProperties));
    }

    public void send(String topic, String payload) {
        send(topic, payload, Collections.emptyMap());
    }

    public void send(String topic, String payload, Map<String, String> headers) {
        Objects.requireNonNull(topic, "topic must not be null");
        Objects.requireNonNull(payload, "payload must not be null");

        String messageKey = UniqueIdGenerator.generate();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, messageKey, payload);
        if (headers != null) {
            headers.forEach((name, value) -> record.headers()
                    .add(name, value == null ? null : value.getBytes(StandardCharsets.UTF_8)));
        }

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                LOGGER.error("Delivery failed for topic={} key={}", topic, messageKey, exception);
            } else {
                LOGGER.info(
                        "Message delivered | topic={} partition={} offset={} key={}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        messageKey
                );
            }
        });

        producer.flush();
        LOGGER.debug("Queued message | topic={} | key={}", topic, messageKey);
    }

    public String getDefaultTopic() {
        return kafkaProperties.getDefaultTopic();
    }

    private Map<String, Object> producerProperties(KafkaClientProperties properties) {
        return Map.ofEntries(
                Map.entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers()),
                Map.entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class),
                Map.entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class),
                Map.entry(ProducerConfig.LINGER_MS_CONFIG, 10),
                Map.entry(ProducerConfig.BATCH_SIZE_CONFIG, 32_768),
                Map.entry(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"),
                Map.entry(ProducerConfig.ACKS_CONFIG, "all"),
                Map.entry(ProducerConfig.RETRIES_CONFIG, 5),
                Map.entry(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true),
                Map.entry(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5),
                Map.entry(ProducerConfig.BUFFER_MEMORY_CONFIG, 33_554_432L) // 32MB
        );
    }

    @PreDestroy
    public void closeProducer() {
        if (closed.compareAndSet(false, true)) {
            LOGGER.info("Closing Kafka producer");
            producer.close();
        }
    }
}

