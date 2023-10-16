package com.toannq.test.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.toannq.test.core*", "com.toannq.test.commons*"})
public class TestApplication {


    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
