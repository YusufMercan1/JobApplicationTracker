package org.example.jobapplicationtracker.security;

import org.example.jobapplicationtracker.model.Role;
import org.example.jobapplicationtracker.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SecurityRoleTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void userToken_shouldNotHaveAdminRole() {

        User user = new User();
        user.setUsername("testuser");
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);

        String role = jwtService.extractRole(token);

        assertThat(role).isNotEqualTo("ADMIN");
        assertThat(role).isEqualTo("USER");
    }

    @Test
    void adminToken_shouldHaveAdminRole() {

        User admin = new User();
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        String token = jwtService.generateToken(admin);

        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("ADMIN");
    }
}
