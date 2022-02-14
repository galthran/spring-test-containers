package com.jarosinski.springtestcontainers;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainerWrapper extends KafkaContainer {

    private static final String IMAGE_VERSION = "confluentinc/cp-kafka:7.0.1";
    private static KafkaContainerWrapper container;

    private KafkaContainerWrapper() {
        super(DockerImageName.parse(IMAGE_VERSION));
    }

    static KafkaContainer getInstance() {
        if (container == null) {
            container = new KafkaContainerWrapper();
            container.start();
            container.setupSpringProperties();
        }
        return container;
    }

    @Override
    public void close() {
        super.close();
    }

    private void setupSpringProperties() {
        String address = kafkaContainerAddress();
        setupBrokerAddress(address);
    }

    private String kafkaContainerAddress() {
        return container.getBootstrapServers();
    }

    private void setupBrokerAddress(String address) {
        System.setProperty("spring.kafka.bootstrap-servers", address);
    }
}
