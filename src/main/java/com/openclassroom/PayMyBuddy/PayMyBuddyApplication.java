package com.openclassroom.PayMyBuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the PayMyBuddy Spring Boot application.
 */
@SpringBootApplication
public class PayMyBuddyApplication {
    /**
     * The main method, which serves as the entry point of the application.
     *
     * @param args command-line arguments passed during application startup.
     */
    public static void main(String[] args) {
        SpringApplication.run(PayMyBuddyApplication.class, args);
    }
}
