package org.example.jobapplicationtracker.dto;

import org.example.jobapplicationtracker.model.Status;

import java.time.LocalDate;

public record UpdateJobApplicationRequest(
        String companyName,
        String role,
        Status status,
        LocalDate appliedDate,
        LocalDate deadline,
        String notes
) {}
