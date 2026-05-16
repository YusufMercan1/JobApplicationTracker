package org.example.jobapplicationtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateJobApplicationRequest(

        @NotBlank
        String companyName,

        @NotBlank
        String role,

        @NotNull
        LocalDate appliedDate,

        @NotNull
        LocalDate deadline,

        String notes
) {}
