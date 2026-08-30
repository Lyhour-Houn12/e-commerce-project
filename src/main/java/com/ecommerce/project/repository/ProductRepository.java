package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Category;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Product findByProductName(String productName);

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageable);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageable);


    Page<Product> findByUser(User sellerProduct, Specification<Product> spec, Pageable pageable);
}
