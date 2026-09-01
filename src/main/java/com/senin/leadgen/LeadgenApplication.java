package com.senin.leadgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LeadgenApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeadgenApplication.class, args);
    }
}
