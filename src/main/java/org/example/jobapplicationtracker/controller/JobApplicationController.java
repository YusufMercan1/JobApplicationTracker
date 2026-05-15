package org.example.jobapplicationtracker.controller;

import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.service.JobApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.jobapplicationtracker.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    // Vi behöver bara service här nu!
    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<JobApplication> create(@RequestBody JobApplication application) {
        return ResponseEntity.ok(service.create(application));
    }

    @GetMapping
    public List<JobApplication> getAll() {
        return service.getAllForCurrentUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/status/{status}")
    public List<JobApplication> getByStatus(@PathVariable Status status) {
        return service.getByStatus(status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplication> update(@PathVariable Long id, @RequestBody JobApplication job) {
        return ResponseEntity.ok(service.update(id, job));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}