package org.example.jobapplicationtracker.controller;

import org.example.jobapplicationtracker.dto.DashboardResponse;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.service.EmailService;
import org.example.jobapplicationtracker.service.JobApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "http://localhost:4200")
public class JobApplicationController {

    private final JobApplicationService service;
    private final EmailService emailService;

    public JobApplicationController(JobApplicationService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<JobApplication> create(@RequestBody JobApplication application) {
        return ResponseEntity.ok(service.create(application));
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getAll() {
        return ResponseEntity.ok(service.getAllForCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplication>> getByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(service.getByStatus(status));
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

    @GetMapping("/stats/overdue")
    public ResponseEntity<List<JobApplication>> getOverdue() {
        return ResponseEntity.ok(service.getOverdueApplications());
    }

    @GetMapping("/stats/active")
    public ResponseEntity<List<JobApplication>> getActive() {
        return ResponseEntity.ok(service.getActiveApplications());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(service.getDashboard());
    }
    @PostMapping("/{id}/send-test-email")
    public ResponseEntity<Void> sendTestEmail(@PathVariable Long id) {
        JobApplication job = service.getById(id);

        String userEmail = job.getUser().getEmail();
        String deadlineStr = job.getDeadline() != null ? job.getDeadline().toString() : "Inget datum satt";

        emailService.sendDeadlineReminder(userEmail, job.getCompanyName(), deadlineStr);

        return ResponseEntity.ok().build();
    }
}