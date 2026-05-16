package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.dto.CreateJobApplicationRequest;
import org.example.jobapplicationtracker.dto.DashboardResponse;
import org.example.jobapplicationtracker.dto.JobApplicationResponse;
import org.example.jobapplicationtracker.dto.UpdateJobApplicationRequest;
import org.example.jobapplicationtracker.exception.JobApplicationNotFoundException;
import org.example.jobapplicationtracker.exception.UnauthorizedAccessException;
import org.example.jobapplicationtracker.exception.UserNotFoundException;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.InterviewRepository;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobApplicationService {

    private static final Logger logger =
            LoggerFactory.getLogger(JobApplicationService.class);

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    public JobApplicationService(
            JobApplicationRepository repository,
            UserRepository userRepository,
            InterviewRepository interviewRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
    }


    public JobApplicationResponse create(CreateJobApplicationRequest req) {

        User currentUser = getCurrentUser();

        JobApplication job = new JobApplication();
        job.setCompanyName(req.companyName());
        job.setRole(req.role());
        job.setAppliedDate(req.appliedDate());
        job.setDeadline(req.deadline());
        job.setNotes(req.notes());
        job.setStatus(Status.APPLIED);
        job.setUser(currentUser);

        JobApplication saved = repository.save(job);

        logger.info("Created job application for user {}", currentUser.getUsername());

        return mapToResponse(saved);
    }


    public List<JobApplicationResponse> getAll() {

        User currentUser = getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public JobApplicationResponse getById(Long id) {

        User currentUser = getCurrentUser();

        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new JobApplicationNotFoundException(id));

        checkOwnership(job, currentUser);

        return mapToResponse(job);
    }

    public JobApplicationResponse update(Long id,
                                         UpdateJobApplicationRequest req) {

        User currentUser = getCurrentUser();

        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new JobApplicationNotFoundException(id));

        checkOwnership(job, currentUser);

        job.setCompanyName(req.companyName());
        job.setRole(req.role());
        job.setStatus(req.status());
        job.setAppliedDate(req.appliedDate());
        job.setDeadline(req.deadline());
        job.setNotes(req.notes());

        JobApplication updated = repository.save(job);

        logger.info("Updated job application id {}", id);

        return mapToResponse(updated);
    }

    public void delete(Long id) {

        User currentUser = getCurrentUser();

        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new JobApplicationNotFoundException(id));

        checkOwnership(job, currentUser);

        repository.delete(job);

        logger.info("Deleted job application id {}", id);
    }


    public List<JobApplicationResponse> getByStatus(Status status) {

        User currentUser = getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .filter(app -> app.getStatus() == status)
                .map(this::mapToResponse)
                .toList();
    }


    public List<JobApplicationResponse> getOverdueApplications() {

        User currentUser = getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> app.getDeadline().isBefore(LocalDate.now()))
                .map(this::mapToResponse)
                .toList();
    }

    public List<JobApplicationResponse> getActiveApplications() {

        User currentUser = getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .filter(app -> app.getStatus() == Status.APPLIED)
                .map(this::mapToResponse)
                .toList();
    }


    public DashboardResponse getDashboard() {

        User currentUser = getCurrentUser();

        List<JobApplication> applications =
                repository.findByUser(currentUser);

        long total = applications.size();
        long applied = applications.stream().filter(app -> app.getStatus() == Status.APPLIED).count();
        long interview = applications.stream().filter(app -> app.getStatus() == Status.INTERVIEW).count();
        long rejected = applications.stream().filter(app -> app.getStatus() == Status.REJECTED).count();

        LocalDate today = LocalDate.now();
        LocalDate in3Days = today.plusDays(3);

        long overdue = applications.stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> app.getDeadline().isBefore(today))
                .count();

        long upcomingDeadlines = applications.stream()
                .filter(app -> app.getDeadline() != null)
                .filter(app -> !app.getDeadline().isBefore(today))
                .filter(app -> !app.getDeadline().isAfter(in3Days))
                .count();

        User user = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        long upcomingMeetings = interviewRepository.countByJobApplicationUserIdAndDateTimeBetween(
                user.getId(), now, now.plusDays(3));

        return new DashboardResponse(
                total,
                applied,
                interview,
                rejected,
                overdue,
                upcomingDeadlines,
                upcomingMeetings
        );
    }

    private JobApplicationResponse mapToResponse(JobApplication job) {

        return new JobApplicationResponse(
                job.getId(),
                job.getCompanyName(),
                job.getRole(),
                job.getStatus(),
                job.getAppliedDate(),
                job.getDeadline(),
                job.getNotes()
        );
    }

    private void checkOwnership(JobApplication job, User user) {

        if (!job.getUser().getUsername()
                .equals(user.getUsername())) {
            throw new UnauthorizedAccessException();
        }
    }

    private User getCurrentUser() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public Page<JobApplicationResponse> getAllPaginated(
            int page,
            int size
    ) {

        User currentUser = getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<JobApplication> applications =
                repository.findByUser(currentUser, pageable);

        return applications.map(this::mapToResponse);
    }

    public List<JobApplicationResponse> getAllForAdmin() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
