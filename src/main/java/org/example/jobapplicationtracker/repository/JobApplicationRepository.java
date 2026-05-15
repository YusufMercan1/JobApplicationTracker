package org.example.jobapplicationtracker.repository;
import org.example.jobapplicationtracker.model.JobApplication;
import org.example.jobapplicationtracker.model.Status;
import org.example.jobapplicationtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatus(Status status);

    List<JobApplication> findByUserUsername(String username);
}