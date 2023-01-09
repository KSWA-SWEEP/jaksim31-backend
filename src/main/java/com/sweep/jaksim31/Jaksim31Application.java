package com.sweep.jaksim31;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.sweep.jaksim31.entity.*")
public class Jaksim31Application {

    public static void main(String[] args) {

        SpringApplication.run(Jaksim31Application.class, args);

    }

}
