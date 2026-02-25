package com.example;

import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create an Admin user natively if none exists
        if (userRepository.findByEmail("admin@smartcity.com").isEmpty()) {
            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@smartcity.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .points(0)
                    .active(true)
                    .build();
            userRepository.save(admin);

            System.out.println("==========================================");
            System.out.println("Default Admin Account Created!");
            System.out.println("Email: admin@smartcity.com");
            System.out.println("Password: admin123");
            System.out.println("==========================================");
        }

        // Also create a test staff user if missing
        if (userRepository.findByEmail("staff@smartcity.com").isEmpty()) {
            User staff = User.builder()
                    .name("Test Staff Worker")
                    .email("staff@smartcity.com")
                    .password(passwordEncoder.encode("staff123"))
                    .role(Role.STAFF)
                    .points(0)
                    .active(true)
                    .build();
            userRepository.save(staff);
            System.out.println("Default Staff Account Created (staff@smartcity.com / staff123)");
        }

        // And a generic test citizen user
        if (userRepository.findByEmail("citizen@test.com").isEmpty()) {
            User citizen = User.builder()
                    .name("Test Citizen")
                    .email("citizen@test.com")
                    .password(passwordEncoder.encode("citizen123"))
                    .role(Role.CITIZEN)
                    .points(95) // almost have digital badge
                    .active(true)
                    .build();
            userRepository.save(citizen);
            System.out.println("Default Citizen Account Created (citizen@test.com / citizen123)");
        }
    }
}
