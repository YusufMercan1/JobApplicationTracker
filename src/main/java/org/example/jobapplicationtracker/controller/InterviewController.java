package org.example.jobapplicationtracker.controller;

import lombok.RequiredArgsConstructor;
import org.example.jobapplicationtracker.dto.InterviewResponse;
import org.example.jobapplicationtracker.model.Interview;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.example.jobapplicationtracker.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InterviewController {

    private final InterviewService interviewService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<InterviewResponse>> getMyMeetings() {
        return ResponseEntity.ok(interviewService.getMeetingsForUser(getCurrentUser()));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<InterviewResponse>> getMeetingsForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(interviewService.getMeetingsForJob(jobId, getCurrentUser()));
    }

    @PostMapping("/{jobId}")
    public ResponseEntity<InterviewResponse> bookMeeting(
            @PathVariable Long jobId,
            @RequestBody Interview interview) {
        return ResponseEntity.ok(interviewService.bookMeeting(jobId, interview, getCurrentUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMeeting(@PathVariable Long id) {
        interviewService.cancelMeeting(id, getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Användaren hittades inte"));
    }
}
