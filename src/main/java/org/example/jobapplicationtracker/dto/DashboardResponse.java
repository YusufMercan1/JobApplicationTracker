package org.example.jobapplicationtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private long total;
    private long applied;
    private long interview;
    private long offer;
    private long rejected;
    private long overdue;
}
