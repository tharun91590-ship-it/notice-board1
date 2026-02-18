package com.tpgit.noticeboard.config;

import com.tpgit.noticeboard.entity.Role;
import com.tpgit.noticeboard.entity.User;
import com.tpgit.noticeboard.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUser("admin@gmail.com", "admin123", Role.PRINCIPAL);
        seedUser("hod@gmail.com", "hod123", Role.HOD);
        seedUser("placement@gmail.com", "placement123", Role.PLACEMENT);
        seedUser("examcell@gmail.com", "examcell123", Role.EXAMCELL);
        seedUser("events@gmail.com", "events123", Role.EVENTS);
        seedUser("hostel@gmail.com", "hostel123", Role.HOSTEL);
        seedUser("library@gmail.com", "library123", Role.LIBRARY);
        seedUser("sports@gmail.com", "sports123", Role.SPORTS);
    }

    private void seedUser(String email, String plainPassword, Role role) {
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setEmail(email);
        user.setRole(role);

        if (user.getPassword() == null || !passwordEncoder.matches(plainPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(plainPassword));
        }

        userRepository.save(user);
    }
}
