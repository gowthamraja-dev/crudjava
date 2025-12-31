package org.example.crudjava.infrastructure.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public class KafkaClientProperties {

    /**
     * Comma separated list of Kafka brokers, e.g.
     * localhost:39092,localhost:39093,localhost:39094.
     */
    private String bootstrapServers;

    /**
     * Default consumer group id used by the sample consumer service.
     */
    private String consumerGroupId = "crudjava-sample-group";

    /**
     * Optional default topic that demo endpoints can use if no topic parameter
     * is supplied.
     */
    private String defaultTopic = "crudjava-sample-topic";

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }
}

