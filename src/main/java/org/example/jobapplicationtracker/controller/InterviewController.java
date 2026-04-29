package org.example.jobapplicationtracker.controller;

import lombok.RequiredArgsConstructor;
import org.example.jobapplicationtracker.model.Interview;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.example.jobapplicationtracker.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final UserRepository userRepository;

    @PostMapping("/{jobId}")
    public ResponseEntity<Interview> bookMeeting(@PathVariable Long jobId, @RequestBody Interview interview) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Användaren hittades inte"));

        return ResponseEntity.ok(interviewService.bookMeeting(jobId, interview, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMeeting(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Användaren hittades inte"));

        interviewService.cancelMeeting(id, currentUser);

        return ResponseEntity.ok().build();
    }
}