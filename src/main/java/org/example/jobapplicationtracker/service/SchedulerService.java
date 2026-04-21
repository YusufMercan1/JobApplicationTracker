package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SchedulerService {

    private final JobApplicationRepository repository;

    public SchedulerService(JobApplicationRepository repository) {
        this.repository = repository;
    }


    @Scheduled(cron = "0 0 9 * * *")//körs varje dag 09:00
    public void checkDeadlines() {

        System.out.println("SCHEDULER IS RUNNING");

        LocalDate today = LocalDate.now();
        LocalDate in3Days = today.plusDays(3);

        List<JobApplication> upcoming = repository.findAll().stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> !app.getDeadline().isAfter(in3Days))
                .toList();

        for (JobApplication app : upcoming) {
            System.out.println("⏰ REMINDER: " +
                    app.getCompanyName() +
                    " deadline: " +
                    app.getDeadline());
        }
    }
}
