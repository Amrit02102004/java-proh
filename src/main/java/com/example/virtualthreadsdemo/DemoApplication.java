package com.example.virtualthreadsdemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Optional;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // This Bean will run on startup.
    // It creates a default "user" / "password" in the database
    // so our /login endpoint has a user to find.
    @Bean
    public CommandLineRunner loadData(UserRepository userRepository) {
        return (args) -> {
            // Check if user already exists
            Optional<User> existingUser = userRepository.findByUsername("user");
            
            if (existingUser.isEmpty()) {
                // Create and save a new user
                User newUser = new User();
                newUser.setUsername("user");
                // In a real app, ALWAYS hash passwords. For this demo, plaintext is fine.
                newUser.setPassword("password");
                userRepository.save(newUser);
                System.out.println("--- Created default user: 'user' with password: 'password' ---");
            } else {
                System.out.println("--- Default user 'user' already exists. ---");
            }
        };
    }
}
