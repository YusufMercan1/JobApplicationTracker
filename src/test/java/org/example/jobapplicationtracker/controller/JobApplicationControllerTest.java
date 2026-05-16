package org.example.jobapplicationtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jobapplicationtracker.dto.CreateJobApplicationRequest;
import org.example.jobapplicationtracker.dto.UpdateJobApplicationRequest;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.Role;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.JobApplicationRepository;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.example.jobapplicationtracker.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long applicationId;

    @BeforeEach
    void setUp() {

        jobRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(encoder.encode("1234"));
        user.setEmail("test@test.com");
        user.setRole(Role.USER);

        userRepository.save(user);

        token = jwtService.generateToken(user);

        JobApplication app = new JobApplication();
        app.setCompanyName("Spotify");
        app.setRole("Backend Developer");
        app.setAppliedDate(LocalDate.now());
        app.setDeadline(LocalDate.now().plusDays(7));
        app.setStatus(Status.APPLIED);
        app.setUser(user);

        applicationId = jobRepository.save(app).getId();
    }

    @Test
    void createApplication_shouldReturn200() throws Exception {

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(
                        "Spotify",
                        "Backend Developer",
                        LocalDate.now(),
                        LocalDate.now().plusDays(7),
                        "Important application"
                );

        mockMvc.perform(post("/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_shouldReturn200() throws Exception {

        mockMvc.perform(get("/applications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void updateApplication_shouldReturn200() throws Exception {

        UpdateJobApplicationRequest request =
                new UpdateJobApplicationRequest(
                        "Google",
                        "Senior Dev",
                        Status.INTERVIEW,
                        LocalDate.now(),
                        LocalDate.now().plusDays(10),
                        "Updated"
                );

        mockMvc.perform(put("/applications/" + applicationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteApplication_shouldReturn200() throws Exception {

        mockMvc.perform(delete("/applications/" + applicationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturn401_whenNoToken() throws Exception {

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(
                        "Spotify",
                        "Dev",
                        LocalDate.now(),
                        LocalDate.now().plusDays(5),
                        "test"
                );

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_shouldReturn403_forUser() throws Exception {

        mockMvc.perform(get("/applications/admin/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_shouldFailValidation_whenCompanyNameMissing() throws Exception {

        String invalidJson = """
        {
          "role": "Dev",
          "appliedDate": "2026-01-01",
          "deadline": "2026-01-10",
          "notes": "test"
        }
        """;

        mockMvc.perform(post("/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
