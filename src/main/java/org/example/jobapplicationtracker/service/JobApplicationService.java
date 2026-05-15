package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.dto.DashboardResponse;
import org.example.jobapplicationtracker.dto.JobApplicationResponse;
import org.example.jobapplicationtracker.exception.JobApplicationNotFoundException;
import org.example.jobapplicationtracker.exception.UnauthorizedAccessException;
import org.example.jobapplicationtracker.exception.UserNotFoundException;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobApplicationService {

    private static final Logger logger =
            LoggerFactory.getLogger(JobApplicationService.class);

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository repository,
                                 UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public JobApplication create(JobApplication job) {

        User currentUser = getCurrentUser();

        logger.info("Creating job application for user: {}",
                currentUser.getUsername());

        job.setUser(currentUser);

        return repository.save(job);
    }

    public List<JobApplicationResponse> getAll() {

        User currentUser = getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .map(job -> new JobApplicationResponse(
                        job.getId(),
                        job.getCompanyName(),
                        job.getRole(),
                        job.getStatus(),
                        job.getAppliedDate(),
                        job.getDeadline(),
                        job.getNotes()
                ))
                .toList();
    }

    public JobApplication getById(Long id) {

        User currentUser = getCurrentUser();

        JobApplication job = repository.findById(id)
                .orElseThrow(() ->
                        new JobApplicationNotFoundException(id));

        if (!job.getUser().getUsername()
                .equals(currentUser.getUsername())) {

            throw new UnauthorizedAccessException();
        }

        return job;
    }

    public List<JobApplication> getByStatus(Status status) {

        User currentUser = getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .filter(app -> app.getStatus() == status)
                .toList();
    }

    public JobApplication update(Long id, JobApplication updatedJob) {

        User currentUser = getCurrentUser();

        JobApplication job = repository.findById(id)
                .orElseThrow(() ->
                        new JobApplicationNotFoundException(id));

        if (!job.getUser().getUsername()
                .equals(currentUser.getUsername())) {

            throw new UnauthorizedAccessException();
        }

        job.setCompanyName(updatedJob.getCompanyName());
        job.setRole(updatedJob.getRole());
        job.setStatus(updatedJob.getStatus());
        job.setAppliedDate(updatedJob.getAppliedDate());
        job.setDeadline(updatedJob.getDeadline());
        job.setNotes(updatedJob.getNotes());

        logger.info("Updated job application with id: {}", id);

        return repository.save(job);
    }

    public void delete(Long id) {

        User currentUser = getCurrentUser();

        JobApplication job = repository.findById(id)
                .orElseThrow(() ->
                        new JobApplicationNotFoundException(id));

        if (!job.getUser().getUsername()
                .equals(currentUser.getUsername())) {

            throw new UnauthorizedAccessException();
        }

        logger.info("Deleted job application with id: {}", id);

        repository.delete(job);
    }

    public List<JobApplication> getOverdueApplications() {

        User currentUser = getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> app.getDeadline()
                        .isBefore(LocalDate.now()))
                .toList();
    }

    public List<JobApplication> getActiveApplications() {

        User currentUser = getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .filter(app -> app.getStatus() == Status.APPLIED)
                .toList();
    }

    public DashboardResponse getDashboard() {

        User currentUser = getCurrentUser();

        List<JobApplication> applications =
                repository.findByUser(currentUser);

        long total = applications.size();

        long applied = applications.stream()
                .filter(app -> app.getStatus() == Status.APPLIED)
                .count();

        long interview = applications.stream()
                .filter(app -> app.getStatus() == Status.INTERVIEW)
                .count();

        long offer = applications.stream()
                .filter(app -> app.getStatus() == Status.OFFER)
                .count();

        long rejected = applications.stream()
                .filter(app -> app.getStatus() == Status.REJECTED)
                .count();

        long overdue = applications.stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> app.getDeadline()
                        .isBefore(LocalDate.now()))
                .count();

        return new DashboardResponse(
                total,
                applied,
                interview,
                offer,
                rejected,
                overdue
        );
    }

    private User getCurrentUser() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(username));
    }
}
