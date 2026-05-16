package org.example.jobapplicationtracker.dto;

public record DashboardResponse(
        long total,
        long applied,
        long interview,
        long offer,
        long rejected,
        long overdue
) {}
