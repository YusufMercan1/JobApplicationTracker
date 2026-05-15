package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.dto.DashboardResponse;
import org.example.jobapplicationtracker.dto.JobApplicationResponse;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public JobApplication create(JobApplication job) {
        job.setUser(getCurrentUser());
        return repository.save(job);
    }

    public List<JobApplication> getAllForCurrentUser() {
        return repository.findByUserUsername(getCurrentUser().getUsername());
    }

    public List<JobApplicationResponse> getAll() {
        return repository.findByUserUsername(getCurrentUser().getUsername())
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

    public List<JobApplication> getByStatus(Status status) {
        return repository.findByUserUsername(getCurrentUser().getUsername()).stream()
                .filter(app -> app.getStatus() == status)
                .toList();
    }

    public JobApplication update(Long id, JobApplication updatedJob) {
        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        // Säkerhetskoll
        if (!job.getUser().getUsername().equals(getCurrentUser().getUsername())) {
            throw new RuntimeException("Unauthorized");
        }

        job.setCompanyName(updatedJob.getCompanyName());
        job.setRole(updatedJob.getRole());
        job.setStatus(updatedJob.getStatus());
        job.setAppliedDate(updatedJob.getAppliedDate());
        job.setDeadline(updatedJob.getDeadline());
        job.setNotes(updatedJob.getNotes());

        return repository.save(job);
    }

    public void delete(Long id) {
        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!job.getUser().getUsername().equals(getCurrentUser().getUsername())) {
            throw new RuntimeException("Unauthorized");
        }

        repository.deleteById(id);
    }

    public JobApplication getById(Long id) {
        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!job.getUser().getUsername().equals(getCurrentUser().getUsername())) {
            throw new RuntimeException("Unauthorized");
        }
        return job;
    }

    public List<JobApplication> getOverdueApplications() {
        return repository.findByUserUsername(getCurrentUser().getUsername()).stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> app.getDeadline().isBefore(LocalDate.now()))
                .toList();
    }

    public List<JobApplication> getActiveApplications() {
        return repository.findByUserUsername(getCurrentUser().getUsername()).stream()
                .filter(app -> app.getStatus() == Status.APPLIED)
                .toList();
    }

    public DashboardResponse getDashboard() {
        List<JobApplication> applications = repository.findByUserUsername(getCurrentUser().getUsername());

        long total = applications.size();
        long applied = applications.stream().filter(app -> app.getStatus() == Status.APPLIED).count();
        long interview = applications.stream().filter(app -> app.getStatus() == Status.INTERVIEW).count();
        long offer = applications.stream().filter(app -> app.getStatus() == Status.OFFER).count();
        long rejected = applications.stream().filter(app -> app.getStatus() == Status.REJECTED).count();

        long overdue = applications.stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> app.getDeadline().isBefore(LocalDate.now()))
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
}