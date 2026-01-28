package com.ecommerce.project.controller;


import com.ecommerce.project.entity.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "/api/public/categories")
public class CategoryController {

    private final CategoryService categoryService;
    public  CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping()
    //@RequestMapping(value = "/api/public/category", method = RequestMethod.POST)
    public String createCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        return "Category created successfully";
    }
    @GetMapping()
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }
    @DeleteMapping("{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try{
            String status = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
        }
        catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category, @PathVariable Long categoryId) {
        try{
            String status = categoryService.updateCategory(category, categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
        }
        catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }
}
