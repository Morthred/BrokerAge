package com.ing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class BrokerageApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrokerageApplication.class, args);
    }
}