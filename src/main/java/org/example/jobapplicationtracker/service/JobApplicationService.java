package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public JobApplication create(JobApplication job) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Användare hittades inte"));
        job.setUser(user);
        return repository.save(job);
    }

    public List<JobApplication> getAllForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByUserUsername(username);
    }

    public JobApplication getById(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jobbet hittades inte"));

        if (!job.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Du har inte tillgång till detta jobb");
        }
        return job;
    }

    public List<JobApplication> getByStatus(org.example.jobapplicationtracker.model.Status status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByUserUsername(username).stream()
                .filter(app -> app.getStatus().equals(status))
                .toList();
    }

    public JobApplication update(Long id, JobApplication updatedJob) {
        JobApplication job = getById(id);

        job.setCompanyName(updatedJob.getCompanyName());
        job.setRole(updatedJob.getRole());
        job.setStatus(updatedJob.getStatus());
        job.setAppliedDate(updatedJob.getAppliedDate());

        return repository.save(job);
    }

    public void delete(Long id) {
        JobApplication job = getById(id);
        repository.delete(job);
    }
}