package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.dto.CreateJobApplicationRequest;
import org.example.jobapplicationtracker.dto.DashboardResponse;
import org.example.jobapplicationtracker.dto.JobApplicationResponse;
import org.example.jobapplicationtracker.dto.UpdateJobApplicationRequest;
import org.example.jobapplicationtracker.exception.JobApplicationNotFoundException;
import org.example.jobapplicationtracker.exception.UnauthorizedAccessException;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.Role;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobApplicationService service;

    private User currentUser;
    private JobApplication jobApplication;

    @BeforeEach
    void setUp() {
        // Skapa en testanvändare
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testuser");
        currentUser.setEmail("test@test.com");
        currentUser.setRole(Role.USER);

        // Sätt upp SecurityContext så getCurrentUser() fungerar
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("testuser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Skapa en test-jobapplication
        jobApplication = new JobApplication();
        jobApplication.setId(1L);
        jobApplication.setCompanyName("Spotify");
        jobApplication.setRole("Backend Developer");
        jobApplication.setStatus(Status.APPLIED);
        jobApplication.setAppliedDate(LocalDate.now());
        jobApplication.setDeadline(LocalDate.now().plusDays(10));
        jobApplication.setNotes("Spännande tjänst");
        jobApplication.setUser(currentUser);
    }

    // ─── CREATE ───────────────────────────────────────────────────

    @Test
    void create_shouldSaveAndReturnResponse() {
        CreateJobApplicationRequest req = new CreateJobApplicationRequest(
                "Spotify", "Backend Developer",
                LocalDate.now(), LocalDate.now().plusDays(10), "Spännande tjänst"
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.save(any())).thenReturn(jobApplication);

        JobApplicationResponse response = service.create(req);

        assertThat(response.companyName()).isEqualTo("Spotify");
        assertThat(response.role()).isEqualTo("Backend Developer");
        assertThat(response.status()).isEqualTo(Status.APPLIED);
        verify(repository, times(1)).save(any());
    }

    // ─── GET ALL ──────────────────────────────────────────────────

    @Test
    void getAll_shouldReturnOnlyCurrentUsersApplications() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findByUser(currentUser)).thenReturn(List.of(jobApplication));

        List<JobApplicationResponse> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).companyName()).isEqualTo("Spotify");
    }

    @Test
    void getAll_shouldReturnEmptyListWhenNoApplications() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findByUser(currentUser)).thenReturn(List.of());

        List<JobApplicationResponse> result = service.getAll();

        assertThat(result).isEmpty();
    }

    // ─── GET BY ID ────────────────────────────────────────────────

    @Test
    void getById_shouldReturnApplicationWhenOwner() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findById(1L)).thenReturn(Optional.of(jobApplication));

        JobApplicationResponse response = service.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.companyName()).isEqualTo("Spotify");
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(JobApplicationNotFoundException.class);
    }

    @Test
    void getById_shouldThrowWhenNotOwner() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");
        jobApplication.setUser(otherUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findById(1L)).thenReturn(Optional.of(jobApplication));

        assertThatThrownBy(() -> service.getById(1L))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    // ─── UPDATE ───────────────────────────────────────────────────

    @Test
    void update_shouldUpdateAndReturnResponse() {
        UpdateJobApplicationRequest req = new UpdateJobApplicationRequest(
                "Spotify", "Senior Developer", Status.INTERVIEW,
                LocalDate.now(), LocalDate.now().plusDays(5), "Uppdaterad"
        );

        jobApplication.setRole("Senior Developer");
        jobApplication.setStatus(Status.INTERVIEW);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findById(1L)).thenReturn(Optional.of(jobApplication));
        when(repository.save(any())).thenReturn(jobApplication);

        JobApplicationResponse response = service.update(1L, req);

        assertThat(response.status()).isEqualTo(Status.INTERVIEW);
        assertThat(response.role()).isEqualTo("Senior Developer");
    }

    @Test
    void update_shouldThrowWhenNotOwner() {
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        jobApplication.setUser(otherUser);

        UpdateJobApplicationRequest req = new UpdateJobApplicationRequest(
                "Spotify", "Dev", Status.APPLIED,
                LocalDate.now(), LocalDate.now().plusDays(5), ""
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findById(1L)).thenReturn(Optional.of(jobApplication));

        assertThatThrownBy(() -> service.update(1L, req))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    // ─── DELETE ───────────────────────────────────────────────────

    @Test
    void delete_shouldDeleteWhenOwner() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findById(1L)).thenReturn(Optional.of(jobApplication));

        service.delete(1L);

        verify(repository, times(1)).delete(jobApplication);
    }

    @Test
    void delete_shouldThrowWhenNotOwner() {
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        jobApplication.setUser(otherUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findById(1L)).thenReturn(Optional.of(jobApplication));

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(UnauthorizedAccessException.class);

        verify(repository, never()).delete(any());
    }

    // ─── GET BY STATUS ────────────────────────────────────────────

    @Test
    void getByStatus_shouldReturnFilteredApplications() {
        JobApplication rejectedJob = new JobApplication();
        rejectedJob.setId(2L);
        rejectedJob.setStatus(Status.REJECTED);
        rejectedJob.setUser(currentUser);
        rejectedJob.setCompanyName("Google");
        rejectedJob.setRole("Dev");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findByUser(currentUser)).thenReturn(List.of(jobApplication, rejectedJob));

        List<JobApplicationResponse> result = service.getByStatus(Status.APPLIED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).companyName()).isEqualTo("Spotify");
    }

    // ─── OVERDUE ──────────────────────────────────────────────────

    @Test
    void getOverdueApplications_shouldReturnExpiredApplications() {
        jobApplication.setDeadline(LocalDate.now().minusDays(1)); // förfallen

        JobApplication futureJob = new JobApplication();
        futureJob.setDeadline(LocalDate.now().plusDays(5));
        futureJob.setStatus(Status.APPLIED);
        futureJob.setUser(currentUser);
        futureJob.setCompanyName("Meta");
        futureJob.setRole("Dev");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findByUser(currentUser)).thenReturn(List.of(jobApplication, futureJob));

        List<JobApplicationResponse> result = service.getOverdueApplications();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).companyName()).isEqualTo("Spotify");
    }

    // ─── DASHBOARD ────────────────────────────────────────────────

    @Test
    void getDashboard_shouldReturnCorrectCounts() {
        JobApplication interviewJob = new JobApplication();
        interviewJob.setStatus(Status.INTERVIEW);
        interviewJob.setUser(currentUser);
        interviewJob.setDeadline(LocalDate.now().minusDays(1)); // overdue

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(repository.findByUser(currentUser)).thenReturn(List.of(jobApplication, interviewJob));

        DashboardResponse dashboard = service.getDashboard();

        assertThat(dashboard.total()).isEqualTo(2);
        assertThat(dashboard.applied()).isEqualTo(1);
        assertThat(dashboard.interview()).isEqualTo(1);
        assertThat(dashboard.overdue()).isEqualTo(1);
    }

    // ─── ADMIN ────────────────────────────────────────────────────

    @Test
    void getAllForAdmin_shouldReturnAllApplications() {
        JobApplication otherJob = new JobApplication();
        otherJob.setId(2L);
        otherJob.setCompanyName("Amazon");
        otherJob.setRole("Dev");
        otherJob.setStatus(Status.OFFER);
        otherJob.setUser(currentUser);

        when(repository.findAll()).thenReturn(List.of(jobApplication, otherJob));

        List<JobApplicationResponse> result = service.getAllForAdmin();

        assertThat(result).hasSize(2);
    }
}
