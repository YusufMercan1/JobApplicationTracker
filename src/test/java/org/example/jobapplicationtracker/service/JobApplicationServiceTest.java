package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.dto.DashboardResponse;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.InterviewRepository;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @InjectMocks
    private JobApplicationService service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void delete_throwsWhenUserDoesNotOwnApplication() {
        User owner = user(1L, "owner");
        User other = user(2L, "other");
        JobApplication job = JobApplication.builder().id(10L).user(owner).build();

        when(jobRepository.findById(10L)).thenReturn(Optional.of(job));
        mockCurrentUser(other);

        assertThrows(RuntimeException.class, () -> service.delete(10L));
        verify(jobRepository, never()).deleteById(any());
    }

    @Test
    void getDashboard_returnsCountsForCurrentUser() {
        User user = user(1L, "testuser");
        mockCurrentUser(user);

        JobApplication applied = JobApplication.builder().status(Status.APPLIED).build();
        JobApplication interview = JobApplication.builder().status(Status.INTERVIEW).build();
        JobApplication rejected = JobApplication.builder().status(Status.REJECTED).build();

        when(jobRepository.findByUserUsername("testuser"))
                .thenReturn(List.of(applied, interview, rejected));
        when(interviewRepository.countByJobApplicationUserIdAndDateTimeBetween(eq(1L), any(), any()))
                .thenReturn(1L);

        DashboardResponse stats = service.getDashboard();

        assertEquals(3, stats.getTotal());
        assertEquals(1, stats.getApplied());
        assertEquals(1, stats.getInterview());
        assertEquals(1, stats.getRejected());
        assertEquals(1, stats.getUpcomingMeetings());
    }

    private User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private void mockCurrentUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getUsername(), null));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }
}
