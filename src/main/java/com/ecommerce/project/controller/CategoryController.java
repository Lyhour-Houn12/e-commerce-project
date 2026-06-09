package com.ecommerce.project.controller;


import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.entity.Category;
import com.ecommerce.project.entity.Role;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class CategoryController {

    private final CategoryService categoryService;
    public  CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @Tag(name = "Category APIs", description = "APIs for managing categories")
    @Operation(summary = "Create Category", description = "API to create new category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category is created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping()
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.addCategory(categoryDTO);
        return new  ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }
    @Tag(name = "Category APIs", description = "APIs for managing categories")
    @GetMapping("/public/categories")
    public ResponseEntity<?> getCategories(@RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                           @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY, required = false) String sortBY,
                                           @RequestParam(value = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder ) {
        return new ResponseEntity<>(categoryService.getAllCategories(pageNumber, pageSize, sortBY, sortOrder), HttpStatus.OK);
    }
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
    }
    @DeleteMapping("{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        String message = String.format("Category with id: %d was deleted", categoryId);
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(message);
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
        CategoryDTO updateCategory = categoryService.updateCategory(categoryDTO, categoryId);
        return ResponseEntity.ok(updateCategory);
    }




}
