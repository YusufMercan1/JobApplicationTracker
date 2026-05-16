package org.example.jobapplicationtracker.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jobapplicationtracker.dto.LoginRequest;
import org.example.jobapplicationtracker.model.Role;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(encoder.encode("1234"));
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        User user = new User();
        user.setUsername("user");
        user.setPassword(encoder.encode("1234"));
        user.setEmail("user@test.com");
        user.setRole(Role.USER);

        userRepository.save(admin);
        userRepository.save(user);
    }

    private String loginAndGetToken(String username) throws Exception {

        LoginRequest login = new LoginRequest(username, "1234");

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("token").asText();
    }

    @Test
    void adminShouldAccessAdminEndpoint() throws Exception {

        String token = loginAndGetToken("admin");

        mockMvc.perform(get("/applications/admin/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userShouldNotAccessAdminEndpoint() throws Exception {

        String token = loginAndGetToken("user");

        mockMvc.perform(get("/applications/admin/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
