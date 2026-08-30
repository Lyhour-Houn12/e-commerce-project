package com.ecommerce.project.payload;

import com.ecommerce.project.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;
    private Set<Role> roles = new HashSet<>();
    private AddressDTO address;
    private CartDTO cart;
}
