package org.example.jobapplicationtracker.controller;

import org.example.jobapplicationtracker.dto.DashboardResponse;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.service.JobApplicationService;
import org.springframework.web.bind.annotation.*;
import org.example.jobapplicationtracker.dto.JobApplicationResponse;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public JobApplication create(@RequestBody JobApplication job) {
        return service.create(job);
    }


    @GetMapping
    public List<JobApplicationResponse> getAll() {
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

    @GetMapping("/stats/overdue")
    public List<JobApplication> getOverdue() {
        return service.getOverdueApplications();

    }

    @GetMapping("/stats/active")
    public List<JobApplication> getActive() {
        return service.getActiveApplications();
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {
        return service.getDashboard();
    }
}

