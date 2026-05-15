package org.example.jobapplicationtracker.controller;

import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.example.jobapplicationtracker.security.JwtService;
import org.example.jobapplicationtracker.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.example.jobapplicationtracker.dto.AuthResponse;
import org.example.jobapplicationtracker.dto.LoginRequest;
import org.example.jobapplicationtracker.dto.RegisterRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
