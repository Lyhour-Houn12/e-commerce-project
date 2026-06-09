package com.ecommerce.project.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequest {
    @NotBlank
    @Size(min = 5, max = 50, message = "Username must be between 5 to 50 characters.")
    private String username;
    @NotBlank
    @Size(min = 5, max = 50, message = "Email must be between 5 to 50 characters.")
    @Email
    private String email;
    @NotBlank
    @Size(min = 7, max = 80, message = "Password must be between 7 to 80 characters.")
    private String password;
    private Set<String> role;
}
