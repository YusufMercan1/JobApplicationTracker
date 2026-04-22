package org.example.jobapplicationtracker.model;

import org.example.jobapplicationtracker.model.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String role;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate appliedDate;
    private LocalDate deadline;

    private String notes;
    @ManyToOne
    private User user;
    private boolean reminderSent = false;
}
