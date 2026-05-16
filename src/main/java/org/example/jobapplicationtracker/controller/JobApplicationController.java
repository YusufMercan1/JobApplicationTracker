package org.example.jobapplicationtracker.controller;

import jakarta.validation.Valid;
import org.example.jobapplicationtracker.dto.CreateJobApplicationRequest;
import org.example.jobapplicationtracker.dto.DashboardResponse;
import org.example.jobapplicationtracker.dto.JobApplicationResponse;
import org.example.jobapplicationtracker.dto.UpdateJobApplicationRequest;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.service.JobApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public JobApplicationResponse create(
            @Valid @RequestBody CreateJobApplicationRequest request
    ) {

        return service.create(request);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<JobApplicationResponse> getAll() {

        return service.getAll();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/paged")
    public Page<JobApplicationResponse> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        return service.getAllPaginated(page, size);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public JobApplicationResponse getById(
            @PathVariable Long id
    ) {

        return service.getById(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/status/{status}")
    public List<JobApplicationResponse> getByStatus(
            @PathVariable Status status
    ) {

        return service.getByStatus(status);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public JobApplicationResponse update(
            @PathVariable Long id,
            @RequestBody UpdateJobApplicationRequest request
    ) {

        return service.update(id, request);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        service.delete(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/stats/overdue")
    public List<JobApplicationResponse> getOverdue() {

        return service.getOverdueApplications();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/stats/active")
    public List<JobApplicationResponse> getActive() {

        return service.getActiveApplications();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {

        return service.getDashboard();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<JobApplicationResponse> getAllForAdmin() {

        return service.getAllForAdmin();
    }
}
