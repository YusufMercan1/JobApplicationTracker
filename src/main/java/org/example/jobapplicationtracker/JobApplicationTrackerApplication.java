package org.example.jobapplicationtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JobApplicationTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobApplicationTrackerApplication.class, args);
    }
}
