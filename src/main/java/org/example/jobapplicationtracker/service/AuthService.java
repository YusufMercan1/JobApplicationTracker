package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.dto.AuthResponse;
import org.example.jobapplicationtracker.dto.LoginRequest;
import org.example.jobapplicationtracker.dto.RegisterRequest;
import org.example.jobapplicationtracker.model.Role;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.example.jobapplicationtracker.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;


    public AuthService(UserRepository repo,
                       PasswordEncoder encoder,
                       JwtService jwtService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {

        if (repo.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));

        if (request.username().equalsIgnoreCase("admin")) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        repo.save(user);
    }
    public AuthResponse login(LoginRequest request) {

        User user = repo.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user); // IMPORTANT FIX

        return new AuthResponse(token);
    }
}
