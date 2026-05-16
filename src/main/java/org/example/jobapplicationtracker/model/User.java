package org.example.jobapplicationtracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.jobapplicationtracker.model.Role;
import java.util.List; // Denna fixar "Cannot resolve symbol 'List'"
import org.example.jobapplicationtracker.model.JobApplication; // Denna fixar 'JobApplication'

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<JobApplication> jobApplications;
}
