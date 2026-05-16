package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.model.Interview;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.InterviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private JobApplicationService jobApplicationService;

    @InjectMocks
    private InterviewService interviewService;

    @Test
    void cancelMeeting_throwsWhenUserDoesNotOwnJob() {
        User owner = new User();
        owner.setId(1L);
        User other = new User();
        other.setId(2L);
        JobApplication job = JobApplication.builder().id(5L).user(owner).build();
        Interview interview = new Interview();
        interview.setId(99L);
        interview.setJobApplication(job);

        when(interviewRepository.findById(99L)).thenReturn(Optional.of(interview));

        assertThrows(RuntimeException.class, () -> interviewService.cancelMeeting(99L, other));
        verify(interviewRepository, never()).delete(any());
    }
}
