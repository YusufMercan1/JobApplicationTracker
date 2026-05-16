package org.example.jobapplicationtracker.security;

import org.example.jobapplicationtracker.model.Role;
import org.example.jobapplicationtracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@test.com");
        user.setRole(Role.ADMIN);
    }

    @Test
    void generateToken_shouldCreateValidToken() {

        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {

        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void extractRole_shouldReturnCorrectRole() {

        String token = jwtService.generateToken(user);

        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("ADMIN");
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {

        String token = jwtService.generateToken(user);

        boolean valid = jwtService.isTokenValid(token);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {

        String invalidToken = "invalid.jwt.token";

        boolean valid = jwtService.isTokenValid(invalidToken);

        assertThat(valid).isFalse();
    }

    @Test
    void extractUsername_shouldReturnNullForInvalidToken() {

        String invalidToken = "invalid.jwt.token";

        String username = jwtService.extractUsername(invalidToken);

        assertThat(username).isNull();
    }

    @Test
    void extractRole_shouldReturnUserFallbackForInvalidToken() {

        String invalidToken = "invalid.jwt.token";

        String role = jwtService.extractRole(invalidToken);

        assertThat(role).isEqualTo("USER");
    }
}
