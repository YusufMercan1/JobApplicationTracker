package org.example.jobapplicationtracker.controller;

import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.security.JwtService;
import org.example.jobapplicationtracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println("Loginförsök: " + user.getUsername());

        return userService.verifyUser(user.getUsername(), user.getPassword())
                .map(dbUser -> {
                    String token = jwtService.generateToken(dbUser.getUsername());
                    return ResponseEntity.ok(token);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Fel användarnamn eller lösenord"));
    }
}
