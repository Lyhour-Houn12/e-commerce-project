package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
        ProductDTO savedProductDTO = productService.addProduct(productDTO,categoryId);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }
    @GetMapping("/public/products")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize",   defaultValue = AppConstants.PAGE_SIZE,   required = false) Integer pageSize,
            @RequestParam(value = "sortBy",     defaultValue = AppConstants.SORT_PRODUCT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir",    defaultValue = AppConstants.SORT_DIR,    required = false) String sortDir
    ){
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir , keyword, category);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
    @GetMapping("/public/category/{categoryId}/products")
    public ResponseEntity<?> getAllProductsByCategory(@PathVariable Long categoryId,
                                                      @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                      @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                      @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_DIR, required = false) String sortBy,
                                                      @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_PRODUCT_BY, required = false) String sortDir){
        ProductResponse productResponse = productService.getProductByCategory(categoryId,pageNumber,pageSize, sortBy, sortDir);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<?> getProductsByKeyword(@PathVariable String keyword,
                                                  @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                  @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_DIR, required = false) String sortBy,
                                                  @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_PRODUCT_BY, required = false) String sortDir){
        ProductResponse productResponse = productService.getProductsByKeyword(keyword,pageNumber,pageSize, sortBy, sortDir);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }
    @PutMapping("/admin/product/{productId}")
    public ResponseEntity<?> updateProduct(@RequestBody ProductDTO productDTO, @PathVariable Long productId){
        ProductDTO updateProductDTO = productService.updateProduct(productDTO, productId);
        return ResponseEntity.ok(updateProductDTO);
    }
    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId){
        ProductDTO deleteProductDTO = productService.deleteProduct(productId);
        return new  ResponseEntity<>(deleteProductDTO, HttpStatus.OK);
    }
    @PutMapping("/admin/product/{productId}/image")
    public ResponseEntity<?> updateProductImage(@PathVariable Long productId, @RequestParam(name = "image") MultipartFile file) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId, file);
        return ResponseEntity.ok(updatedProduct);
    }


}
