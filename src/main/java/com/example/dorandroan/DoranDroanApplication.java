package com.example.dorandroan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.dorandroan.repository")
public class DoranDroanApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoranDroanApplication.class, args);
    }

}
