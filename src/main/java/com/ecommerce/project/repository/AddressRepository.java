package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT add FROM Address add WHERE add.user.userId=?1")
    List<Address> findAddressesByUserId(Long userId);
}
