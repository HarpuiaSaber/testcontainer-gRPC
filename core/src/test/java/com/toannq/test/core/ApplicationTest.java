package com.toannq.test.core;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class ApplicationTest {
    public static ComposeContainer environment = new ComposeContainer(new File("config/compose-test.yml"))
            .withEnv("MODULE_HOME", System.getProperty("user.dir"))
            .withLocalCompose(true);

    static {
        environment.waitingFor("gripmock", Wait.defaultWaitStrategy())
                .waitingFor("postgres", Wait.forLogMessage(".*\\[1\\] LOG:  database system is ready to accept connections\n", 1))
                .start();
    }
}