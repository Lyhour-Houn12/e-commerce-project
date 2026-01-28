package com.ecommerce.project.service;

import com.ecommerce.project.entity.Category;

import java.util.List;

public interface CategoryService {
    void addCategory(Category category);
    List<Category> getAllCategories();
    String deleteCategory(Long categoryId);
    String updateCategory(Category category, Long categoryId);
}
