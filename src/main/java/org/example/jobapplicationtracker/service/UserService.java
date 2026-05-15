package org.example.jobapplicationtracker.service;

import org.example.jobapplicationtracker.model.User;
import org.example.jobapplicationtracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    public User registerUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> verifyUser(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> encoder.matches(rawPassword, user.getPassword()));
    }
}
