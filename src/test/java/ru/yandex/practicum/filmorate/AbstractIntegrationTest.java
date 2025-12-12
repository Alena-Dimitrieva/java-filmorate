package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

//Тест, который запускает и останавливает сервер
public abstract class AbstractIntegrationTest {

    protected static ConfigurableApplicationContext context;

    @BeforeAll
    static void startServer() {
        if (context == null) {
            context = SpringApplication.run(FilmorateApplication.class);
        }
    }

    @AfterAll
    static void stopServer() {
        if (context != null) {
            SpringApplication.exit(context);
        }
    }
}