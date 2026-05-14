package org.example.jobapplicationtracker.controller;

import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.example.jobapplicationtracker.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.jobapplicationtracker.dto.AuthResponse;
import org.example.jobapplicationtracker.dto.LoginRequest;
import org.example.jobapplicationtracker.dto.RegisterRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository repo, JwtService jwtService) {
        this.repo = repo;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));

        return repo.save(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        User dbUser = repo.findByUsername(request.username())
                .orElseThrow();

        if (encoder.matches(request.password(), dbUser.getPassword())) {

            String token = jwtService.generateToken(request.username());

            return new AuthResponse(token);
        }

        throw new RuntimeException("Invalid login");
    }
}
