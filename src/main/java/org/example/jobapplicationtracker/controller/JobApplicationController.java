package org.example.jobapplicationtracker.controller;

import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.service.JobApplicationService;
import org.springframework.web.bind.annotation.*;
import org.example.jobapplicationtracker.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService service;
    private final UserRepository userRepository;

    public JobApplicationController(JobApplicationService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping
    public JobApplication create(@RequestBody JobApplication job) {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Användare hittades inte"));

        job.setUser(currentUser);

        return service.create(job);
    }

    @GetMapping
    public List<JobApplication> getAll() {
        return service.getAll();
    }

    @GetMapping("/status/{status}")
    public List<JobApplication> getByStatus(@PathVariable Status status) {
        return service.getByStatus(status);
    }

    @PutMapping("/{id}")
    public JobApplication update(@PathVariable Long id, @RequestBody JobApplication job) {
        return service.update(id, job);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    @GetMapping("/{id}")
    public JobApplication getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
