package com.example.labresponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LabResponseApplication {
    public static void main(String[] args) {
        SpringApplication.run(LabResponseApplication.class, args);
    }
}
