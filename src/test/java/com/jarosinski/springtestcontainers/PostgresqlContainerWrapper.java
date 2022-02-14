package com.jarosinski.springtestcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresqlContainerWrapper extends PostgreSQLContainer<PostgresqlContainerWrapper> {
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static PostgresqlContainerWrapper container;

    private PostgresqlContainerWrapper() {
        super(IMAGE_VERSION);
    }

    public static PostgresqlContainerWrapper getInstance() {
        if (container == null) {
            container = new PostgresqlContainerWrapper();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
