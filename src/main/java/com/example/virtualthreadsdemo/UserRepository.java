package com.example.virtualthreadsdemo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA will automatically create the query for us
    Optional<User> findByUsername(String username);
}
