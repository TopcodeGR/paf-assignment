package com.paf.exercise.exercise;


import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class IntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> PSQL_CONTAINER =
            new PostgreSQLContainer<>("postgres")
                    .withDatabaseName("testdb").withUsername("test").withPassword("test");


    static {
        PSQL_CONTAINER.start();
    }


    @DynamicPropertySource
    static void psqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", PSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", PSQL_CONTAINER::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/changelog.xml");
        registry.add("spring.liquibase.enabled", () -> true);
    }
}
