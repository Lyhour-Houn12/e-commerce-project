package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o")
    Double getTotalRevenue();

}
