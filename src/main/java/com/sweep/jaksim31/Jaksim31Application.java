package com.sweep.jaksim31;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableFeignClients
@EnableMongoRepositories(basePackages = "com.sweep.jaksim31.domain.*")
public class Jaksim31Application {

//    @PostConstruct
//    public void started() {
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
//    }

    public static void main(String[] args) {

        SpringApplication.run(Jaksim31Application.class, args);

    }

}
