package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.model.Interview;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.repository.InterviewRepository;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.util.ReminderUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SchedulerService {

    private final JobApplicationRepository jobRepository;
    private final InterviewRepository interviewRepository;
    private final EmailService emailService;

    public SchedulerService(
            JobApplicationRepository jobRepository,
            InterviewRepository interviewRepository,
            EmailService emailService) {
        this.jobRepository = jobRepository;
        this.interviewRepository = interviewRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkDeadlinesAndMeetings() {
        checkApplicationDeadlines();
        checkUpcomingMeetings();
    }

    private void checkApplicationDeadlines() {
        List<JobApplication> applications = jobRepository.findAll().stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> ReminderUtils.isDeadlineApproaching(app.getDeadline())
                        || ReminderUtils.isDeadlineOverdue(app.getDeadline()))
                .filter(app -> app.getUser() != null && app.getUser().getEmail() != null)
                .toList();

        for (JobApplication app : applications) {
            if (app.isReminderSent()) {
                continue;
            }

            emailService.sendDeadlineReminder(
                    app.getUser().getEmail(),
                    app.getCompanyName(),
                    app.getDeadline().toString()
            );

            app.setReminderSent(true);
            jobRepository.save(app);
        }
    }

    private void checkUpcomingMeetings() {
        List<Interview> interviews = interviewRepository.findAll().stream()
                .filter(i -> i.getDateTime() != null)
                .filter(i -> ReminderUtils.isMeetingApproaching(i.getDateTime()))
                .filter(i -> i.getJobApplication() != null)
                .filter(i -> i.getJobApplication().getUser() != null)
                .filter(i -> i.getJobApplication().getUser().getEmail() != null)
                .toList();

        for (Interview interview : interviews) {
            if (interview.isReminderSent()) {
                continue;
            }

            JobApplication job = interview.getJobApplication();
            emailService.sendMeetingReminder(
                    job.getUser().getEmail(),
                    job.getCompanyName(),
                    interview.getDateTime().toString(),
                    interview.getLocation() != null ? interview.getLocation() : "Ej angiven"
            );

            interview.setReminderSent(true);
            interviewRepository.save(interview);
        }
    }
}
