package com.ecommerce.project.repository;

import com.ecommerce.project.entity.User;
import com.ecommerce.project.entity.enumerated.RoleApp;
import com.ecommerce.project.payload.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);

    boolean existsByUserName(@NotBlank @Size(min = 5, max = 50, message = "Username must be between 5 to 50 characters.") String username);

    boolean existsByEmail(@NotBlank @Size(min = 5, max = 50, message = "Email must be between 5 to 50 characters.") String email);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    JOIN u.roles r
    WHERE r.roleName = :roleName
    """)
    Page<User> findByRoleName(@Param("roleName") RoleApp roleName, Pageable pageable);
}
