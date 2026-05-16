package org.example.jobapplicationtracker.dto;

import java.time.LocalDateTime;
import java.util.List;

public record InterviewResponse(
        Long id,
        LocalDateTime dateTime,
        String location,
        Long jobApplicationId,
        boolean reminderSent,
        boolean meetingApproaching,
        List<ParticipantResponse> participants
) {
    public record ParticipantResponse(Long id, String name, String email) {}
}
