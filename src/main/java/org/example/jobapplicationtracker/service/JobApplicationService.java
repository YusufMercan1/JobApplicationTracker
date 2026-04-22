package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.jobapplicationtracker.model.User;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository repository,
                                 UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public JobApplication create(JobApplication job) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        System.out.println("JWT USER: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        job.setUser(user);

        return repository.save(job);
    }

    public List<JobApplication> getAll() {
        return repository.findAll();
    }

    public List<JobApplication> getByStatus(Status status) {
        return repository.findByStatus(status);
    }

    public JobApplication update(Long id, JobApplication updatedJob) {
        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        job.setCompanyName(updatedJob.getCompanyName());
        job.setRole(updatedJob.getRole());
        job.setStatus(updatedJob.getStatus());
        job.setAppliedDate(updatedJob.getAppliedDate());
        job.setDeadline(updatedJob.getDeadline());
        job.setNotes(updatedJob.getNotes());

        return repository.save(job);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public JobApplication getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
    public List<JobApplication> getOverdueApplications() {
        return repository.findAll().stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> app.getDeadline().isBefore(java.time.LocalDate.now()))
                .toList();
    }
    public List<JobApplication> getActiveApplications() {
        return repository.findAll().stream()
                .filter(app -> app.getStatus() != Status.REJECTED)
                .toList();
    }
}
