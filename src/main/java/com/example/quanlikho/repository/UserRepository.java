package com.example.quanlikho.repository;

import java.util.Optional;
    
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quanlikho.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
