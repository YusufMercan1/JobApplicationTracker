package org.example.jobapplicationtracker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.jobapplicationtracker.model.Interview;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.InterviewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationService jobApplicationService;

    @Transactional
    public Interview bookMeeting(Long jobId, Interview interview, User currentUser) {
        JobApplication job = jobApplicationService.getById(jobId);

        if (!job.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Behörighet saknas för detta jobb");
        }

        interview.setJobApplication(job);
        if (interview.getParticipants() != null) {
            interview.getParticipants().forEach(p -> p.setInterview(interview));
        }

        return interviewRepository.save(interview);
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
}
