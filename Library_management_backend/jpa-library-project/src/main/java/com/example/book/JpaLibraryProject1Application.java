package com.example.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for the Spring Boot application.
 * This class is the entry point of the Spring Boot application.
 */
@SpringBootApplication
public class JpaLibraryProject1Application {

    /**
     * The main method that starts the Spring Boot application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(JpaLibraryProject1Application.class, args);
    }
}
