package org.example.jobapplicationtracker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.jobapplicationtracker.dto.InterviewResponse;
import org.example.jobapplicationtracker.model.Interview;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.Participant;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.InterviewRepository;
import org.example.jobapplicationtracker.util.ReminderUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationService jobApplicationService;

    @Transactional
    public InterviewResponse bookMeeting(Long jobId, Interview interview, User currentUser) {
        JobApplication job = jobApplicationService.getById(jobId);

        if (!job.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Behörighet saknas för detta jobb");
        }

        interview.setJobApplication(job);
        interview.setReminderSent(false);
        if (interview.getParticipants() != null) {
            interview.getParticipants().forEach(p -> p.setInterview(interview));
        }

        return toResponse(interviewRepository.save(interview));
    }

    @Transactional
    public void cancelMeeting(Long interviewId, User currentUser) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Mötet hittades inte"));

        if (!interview.getJobApplication().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Du har inte behörighet att avboka detta möte");
        }

        interviewRepository.delete(interview);
    }

    @Transactional
    public List<InterviewResponse> getMeetingsForJob(Long jobId, User currentUser) {
        JobApplication job = jobApplicationService.getById(jobId);

        if (!job.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Behörighet saknas");
        }

        return interviewRepository.findByJobApplicationId(jobId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<InterviewResponse> getMeetingsForUser(User currentUser) {
        return interviewRepository.findByJobApplicationUserId(currentUser.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public long countUpcomingMeetingsForUser(User currentUser) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in3Days = now.plusDays(3);
        return interviewRepository.countByJobApplicationUserIdAndDateTimeBetween(
                currentUser.getId(), now, in3Days);
    }

    private InterviewResponse toResponse(Interview interview) {
        List<InterviewResponse.ParticipantResponse> participants =
                interview.getParticipants() == null
                        ? Collections.emptyList()
                        : interview.getParticipants().stream()
                        .map(p -> new InterviewResponse.ParticipantResponse(p.getId(), p.getName(), p.getEmail()))
                        .toList();

        return new InterviewResponse(
                interview.getId(),
                interview.getDateTime(),
                interview.getLocation(),
                interview.getJobApplication().getId(),
                interview.isReminderSent(),
                ReminderUtils.isMeetingApproaching(interview.getDateTime()),
                participants
        );
    }
}
