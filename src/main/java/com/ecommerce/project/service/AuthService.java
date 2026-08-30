package com.ecommerce.project.service;

import com.ecommerce.project.payload.UserResponse;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignUpRequest;
import com.ecommerce.project.security.response.UserInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

public interface AuthService {
    UserInfoResponse login(LoginRequest loginRequest);

    void signUp(SignUpRequest signUpRequest);

    String getUsername(Authentication authentication);

    UserInfoResponse getUserInfo(Authentication authentication);

    UserResponse getAllUserSeller(Pageable pageable);

}
