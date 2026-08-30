package com.ecommerce.project.service.impl;

import com.ecommerce.project.entity.Role;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.entity.enumerated.RoleApp;
import com.ecommerce.project.exception.AuthException;
import com.ecommerce.project.payload.UserDTO;
import com.ecommerce.project.payload.UserResponse;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignUpRequest;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import com.ecommerce.project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder  encoder;
    private final JwtUtils  jwtUtils;
    private final ModelMapper modelMapper;
    @Override
    public UserInfoResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new AuthException("Invalid username and password");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();
        UserInfoResponse  userInfoResponse = new UserInfoResponse(userDetails.getId(), jwtToken, userDetails.getUsername(), userDetails.getEmail(), roles);
        return userInfoResponse;
    }

    @Override
    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            throw new AuthException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AuthException("Error: Email is already taken!");
        }

        // Create new user's account
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(RoleApp.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(RoleApp.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role modRole = roleRepository.findByRoleName(RoleApp.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(RoleApp.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public String getUsername(Authentication authentication) {
        String username = authentication.getName();
        if(username != null){
            return username;
        }else{
            return " ";
        }
    }


    @Override
    public UserInfoResponse getUserInfo(Authentication authentication) {
        UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();
        UserInfoResponse userInfoResponse = new  UserInfoResponse(userDetails.getId(), null, userDetails.getUsername(), userDetails.getEmail(), roles);
        return  userInfoResponse;
    }

    @Override
    public UserResponse getAllUserSeller(Pageable pageable) {
        Page<User> users =  userRepository.findByRoleName(RoleApp.ROLE_SELLER, pageable);
        System.out.println("Total = " + users.getTotalElements());
        System.out.println("Size = " + users.getContent().size());

        for (User u : users.getContent()) {
            System.out.println(u.getUserName());
        }
        List<UserDTO> userDTOS = users.getContent()
                .stream()
                .map(p-> modelMapper.map(p, UserDTO.class))
                .toList();

        UserResponse response = new UserResponse();
        response.setContent(userDTOS);
        response.setTotalElements(users.getTotalElements());
        response.setTotalPages(users.getTotalPages());
        response.setPageNumber(users.getNumber());
        response.setPageSize(users.getSize());
        response.setLastPage(users.isLast());
        return response;
    }


}
