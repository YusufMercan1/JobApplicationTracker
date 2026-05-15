package org.example.jobapplicationtracker.dto;

import java.time.LocalDate;

public record CreateJobApplicationRequest(
        String companyName,
        String role,
        LocalDate appliedDate,
        LocalDate deadline,
        String notes
) {}
