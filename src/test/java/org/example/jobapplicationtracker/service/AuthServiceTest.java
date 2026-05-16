package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.dto.AuthResponse;
import org.example.jobapplicationtracker.dto.LoginRequest;
import org.example.jobapplicationtracker.dto.RegisterRequest;
import org.example.jobapplicationtracker.model.Role;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.example.jobapplicationtracker.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("hashedpassword");
        user.setRole(Role.USER);
    }

    // ─── REGISTER ─────────────────────────────────────────────────

    @Test
    void register_shouldSaveUserWithUserRole() {
        RegisterRequest req = new RegisterRequest("testuser", "test@test.com", "password123");

        when(repo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(encoder.encode("password123")).thenReturn("hashedpassword");

        authService.register(req);

        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldAssignAdminRoleWhenUsernameIsAdmin() {
        RegisterRequest req = new RegisterRequest("admin", "admin@test.com", "password123");

        when(repo.findByUsername("admin")).thenReturn(Optional.empty());
        when(encoder.encode("password123")).thenReturn("hashedpassword");

        authService.register(req);

        verify(repo).save(argThat(savedUser ->
                savedUser.getRole() == Role.ADMIN
        ));
    }

    @Test
    void register_shouldAssignAdminRoleWhenUsernameIsAdminCaseInsensitive() {
        RegisterRequest req = new RegisterRequest("ADMIN", "admin@test.com", "password123");

        when(repo.findByUsername("ADMIN")).thenReturn(Optional.empty());
        when(encoder.encode("password123")).thenReturn("hashedpassword");

        authService.register(req);

        verify(repo).save(argThat(savedUser ->
                savedUser.getRole() == Role.ADMIN
        ));
    }

    @Test
    void register_shouldAssignUserRoleForNormalUser() {
        RegisterRequest req = new RegisterRequest("johndoe", "john@test.com", "password123");

        when(repo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(encoder.encode("password123")).thenReturn("hashedpassword");

        authService.register(req);

        verify(repo).save(argThat(savedUser ->
                savedUser.getRole() == Role.USER
        ));
    }

    @Test
    void register_shouldThrowWhenUsernameAlreadyExists() {
        RegisterRequest req = new RegisterRequest("testuser", "test@test.com", "password123");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username already exists");

        verify(repo, never()).save(any());
    }

    @Test
    void register_shouldEncodePassword() {
        RegisterRequest req = new RegisterRequest("testuser", "test@test.com", "password123");

        when(repo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(encoder.encode("password123")).thenReturn("hashedpassword");

        authService.register(req);

        verify(encoder, times(1)).encode("password123");
        verify(repo).save(argThat(savedUser ->
                savedUser.getPassword().equals("hashedpassword")
        ));
    }

    // ─── LOGIN ────────────────────────────────────────────────────

    @Test
    void login_shouldReturnTokenWhenCredentialsAreCorrect() {
        LoginRequest req = new LoginRequest("testuser", "password123");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(encoder.matches("password123", "hashedpassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(req);

        assertThat(response.token()).isEqualTo("mocked-jwt-token");
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest req = new LoginRequest("nonexistent", "password123");

        when(repo.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void login_shouldThrowWhenPasswordIsWrong() {
        LoginRequest req = new LoginRequest("testuser", "wrongpassword");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(encoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void login_shouldCallGenerateTokenWithCorrectUser() {
        LoginRequest req = new LoginRequest("testuser", "password123");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(encoder.matches("password123", "hashedpassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("mocked-jwt-token");

        authService.login(req);

        verify(jwtService, times(1)).generateToken(user);
    }
}
