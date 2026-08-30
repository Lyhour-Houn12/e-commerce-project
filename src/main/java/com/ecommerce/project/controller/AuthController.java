package com.ecommerce.project.controller;
import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.entity.Role;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.entity.enumerated.RoleApp;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignUpRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import com.ecommerce.project.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> singIn(@Valid @RequestBody LoginRequest  loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


        @PostMapping("/signup")
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
            authService.signUp(signUpRequest);
            return new ResponseEntity<>(new MessageResponse("User registered successfully"), HttpStatus.CREATED);
        }

    @GetMapping("/username")
    public ResponseEntity<String> getUsername(Authentication authentication) {
        String username = authService.getUsername(authentication);
        return ResponseEntity.ok(username);
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUser(Authentication authentication) {
        UserInfoResponse response = authService.getUserInfo(authentication);
        return  ResponseEntity.ok(response);

    }
    @GetMapping("/sellers")
    public ResponseEntity<?> getSellers(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber, @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        Sort sortByOrder = Sort.by(AppConstants.SORT_USERS_BY).descending();
        Pageable pageable = PageRequest.of(pageNumber , pageSize, sortByOrder);
        return ResponseEntity.ok(authService.getAllUserSeller(pageable));
    }



}
