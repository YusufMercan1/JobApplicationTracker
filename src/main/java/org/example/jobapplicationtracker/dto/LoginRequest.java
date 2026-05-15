package org.example.jobapplicationtracker.dto;


public record LoginRequest(
        String username,
        String password
) {}
