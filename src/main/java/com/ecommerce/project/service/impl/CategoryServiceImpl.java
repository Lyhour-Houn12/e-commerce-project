package com.ecommerce.project.service.impl;

import com.ecommerce.project.entity.Category;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public String deleteCategory(Long categoryId) {
        List<Category> categories = categoryRepository.findAll();
        Category category =  categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Category with id: %s not found", categoryId)) );
        categoryRepository.delete(category);
        return "Category with id: " + category.getCategoryId() + " has been deleted";
    }

    @Override
    public String updateCategory(Category category, Long categoryId) {
//        Category categoryToUpdate = categories.stream()
//                .filter(c -> c.getCategoryId().equals(categoryId))
//                .findFirst()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Category with id: %s not found", categoryId)) );
//        categoryToUpdate.setCategoryName(category.getCategoryName());
        List<Category> categories = categoryRepository.findAll();
        Optional<Category> categoryToUpdate = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst();
        if(categoryToUpdate.isPresent()){
            Category existingCategory = categoryToUpdate.get();
            existingCategory.setCategoryName(category.getCategoryName());
            categoryRepository.save(existingCategory);
            return "Category with id: " + existingCategory.getCategoryId() + " has been updated";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Category with id: %s not found", categoryId));

    }
}
