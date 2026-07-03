package com.ftn.backend.configuration;

import com.ftn.backend.enums.Role;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@ftn.tn")) {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("FTN")
                    .email("admin@ftn.tn")
                    .password(passwordEncoder.encode("Admin@2024"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Seeded admin user: admin@ftn.tn / Admin@2024");
        }
    }
}
