package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SchedulerService {

    private final JobApplicationRepository repository;
    private final EmailService emailService;

    public SchedulerService(JobApplicationRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 30000) // var 30 sek för test
    public void checkDeadlines() {

        LocalDate today = LocalDate.now();
        LocalDate in3Days = today.plusDays(3);

        List<JobApplication> upcoming = repository.findAll().stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> !app.getDeadline().isAfter(in3Days))
                .toList();

        for (JobApplication app : upcoming) {

            User user = app.getUser();

            if (user != null && user.getEmail() != null) {

                emailService.sendReminder(
                        user.getEmail(),
                        app.getCompanyName(),
                        app.getDeadline().toString()
                );
            }
        }
    }
}
