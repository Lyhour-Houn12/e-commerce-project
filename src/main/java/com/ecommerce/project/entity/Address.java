package com.ecommerce.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Building name must be at least 5 characters.")
    @Column(name = "building_name")
    private String buildingName;
    @NotBlank
    @Size(min = 5, message = "City name must be at least 5 characters.")
    private String city;
    @NotBlank
    @Size(min = 5, message = "Country name must be at least 5 characters.")
    private String country;
    @NotBlank
    @Size(min = 5, message = "Pin code must be at least 5 characters.")
    private String pinCode;
    @NotBlank
    @Size(min = 5, message = "State name must be at least 5 characters.")
    private String state;
    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters.")
    private String street;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
