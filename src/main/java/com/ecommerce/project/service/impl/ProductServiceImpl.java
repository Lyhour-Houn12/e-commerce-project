package com.ecommerce.project.service.impl;

import com.ecommerce.project.entity.Cart;
import com.ecommerce.project.entity.Category;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.service.FileService;
import com.ecommerce.project.service.ProductService;
import com.ecommerce.project.service.util.Utility;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
        product.setImage("default.png");
        product.setCategory(savedCategory);
        double specialPrice = product.getPrice() -((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.findByProductName(product.getProductName());
        if(savedProduct != null){
            throw new APIException(String.format("Product with name %s already exists", product.getProductName()));
        }
        savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir, String keyword, String category) {
        Sort sortByAndOrder = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);

        Specification<Product> spec = getProductSpecification(keyword, category);

        Page<Product> products = productRepository.findAll(spec, pageable);
        if(products.isEmpty()){
            throw new APIException("Product list is empty");
        }
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> {
                    ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                    productDTO.setImage(constructImage(product.getImage()));
                    return productDTO;
                })
                .toList();
        return Utility.getProductResponse(productDTOs, products);
    }

    private  Specification<Product> getProductSpecification(String keyword, String category) {
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();
        if(keyword != null && !keyword.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
        }

        if(category != null && !category.isEmpty()){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            criteriaBuilder.lower(root.get("category").get("categoryName")),
                            category.toLowerCase()
                    ));
        }
        return spec;
    }

    private String constructImage(String imageName){
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName: imageBaseUrl + "/" + imageName;
    }

    @Override
    public ProductResponse getProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() :  Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Product> pageProduct =  productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        List<Product> products = pageProduct.getContent();
        if(products.isEmpty()){
            throw new APIException(category.getCategoryName() + " category does not have any products");
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        return Utility.getProductResponse(productDTOs, pageProduct);
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sortByAndOrder = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() :  Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Product> products = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageable);

        List<Product> productList = products.getContent();
        List<ProductDTO> productDTOs = productList.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        if(products.isEmpty()){
            throw new APIException(String.format("Product with %s keyword is empty", keyword));
        }
        return Utility.getProductResponse(productDTOs, products);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        // convert productDto to product
        Product product = modelMapper.map(productDTO, Product.class);
        // find the existing product to update
        Product productFromDb = findProductById(productId);
        // update the info of product
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        Double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice() );
        productFromDb.setSpecialPrice(specialPrice);
        // saved from to db
        Product savedProduct =  productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream()
                .map(item ->{
                    CartDTO cartDTO = modelMapper.map(item, CartDTO.class);
                    List<ProductDTO> products = item.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).toList();
                    cartDTO.setProducts(products);
                    return cartDTO;
                }).toList();
        cartDTOS.forEach(cart -> cartService.updateProductInCart(cart.getCartId(), productId));

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDb = findProductById(productId);
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(item -> cartService.deleteProductFromCart(item.getCartId(), productId));
        productRepository.delete(productFromDb);
        return modelMapper.map(productFromDb, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile file) throws IOException {
        // Get the product from db
        Product productFromDb = findProductById(productId);
        // Upload the image to server
        // Get the file name of uploaded image

        String filename = fileService.uploadImage(path, file);
        // Updating the new file name to the product
        productFromDb.setImage(filename);
        // return DTO after mapping product to DTO
        productFromDb = productRepository.save(productFromDb);
        return modelMapper.map(productFromDb, ProductDTO.class);
    }

    private Product findProductById(Long productId){
        return  productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

}
