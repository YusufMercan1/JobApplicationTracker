package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public JobApplication create(JobApplication job) {
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
}
