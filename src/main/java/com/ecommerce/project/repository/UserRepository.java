package com.ecommerce.project.repository;

import com.ecommerce.project.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);

    boolean existsByUserName(@NotBlank @Size(min = 5, max = 50, message = "Username must be between 5 to 50 characters.") String username);

    boolean existsByEmail(@NotBlank @Size(min = 5, max = 50, message = "Email must be between 5 to 50 characters.") String email);
}
