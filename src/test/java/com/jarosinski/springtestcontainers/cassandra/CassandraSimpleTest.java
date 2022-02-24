package com.jarosinski.springtestcontainers.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest
public class CassandraSimpleTest {

    private static final String KEYSPACE_NAME = "test";

    @Autowired
    private CarRepository carRepository;

    @Container
    private static final CassandraContainer cassandra = (CassandraContainer) new CassandraContainer("cassandra:3.11.2")
            .withExposedPorts(9042);

    @BeforeAll
    static void setupCassandraConnectionProperties() {
        System.setProperty("spring.data.cassandra.keyspace-name", KEYSPACE_NAME);
        System.setProperty("spring.data.cassandra.contact-points", cassandra.getContainerIpAddress());
        System.setProperty("spring.data.cassandra.port", String.valueOf(cassandra.getMappedPort(9042)));

        createKeyspace(cassandra.getCluster());
    }

    static void createKeyspace(Cluster cluster) {
        try(Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME + " WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");
        }
    }

    @Test
    void givenCassandraContainer_whenSpringContextIsBootstrapped_thenContainerIsRunningWithNoExceptions() {
        assertThat(cassandra.isRunning()).isTrue();
    }

    @Test
    void createNewCar() {
        UUID carId = UUIDs.timeBased();
        Car newCar = new Car(carId, "Nissan", "Qashqai", 2018);

        carRepository.save(newCar);

        List<Car> savedCars = carRepository.findAllById(List.of(carId));
        assertThat(savedCars.get(0)).isEqualTo(newCar);
    }

    @Test
    void UpdateCar() {
        UUID carId = UUIDs.timeBased();
        Car existingCar = carRepository.save(new Car(carId, "Nissan", "Qashqai", 2018));

        existingCar.setModel("X-Trail");
        carRepository.save(existingCar);

        List<Car> savedCars = carRepository.findAllById(List.of(carId));
        assertThat(savedCars.get(0).getModel()).isEqualTo("X-Trail");
    }

    @Test
    void deleteCar() {
        UUID carId = UUIDs.timeBased();
        Car existingCar = carRepository.save(new Car(carId, "Nissan", "Qashqai", 2018));

        carRepository.delete(existingCar);

        List<Car> savedCars = carRepository.findAllById(List.of(carId));
        assertThat(savedCars.isEmpty()).isTrue();
    }
}
