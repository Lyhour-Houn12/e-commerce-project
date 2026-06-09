package com.ecommerce.project.controller;

import com.ecommerce.project.entity.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    private final AuthUtil authUtil;
    private final CartRepository cartRepository;

    @PostMapping("/carts/product/{productId}/quantity/{quantity}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDTO cart = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }
    @GetMapping("/carts")
    public ResponseEntity<?> getCarts(){
        List<CartDTO> carts = cartService.getCarts();
        return new ResponseEntity<>(carts, HttpStatus.FOUND);
    }
    @GetMapping("/cart/{cartId}")
    public ResponseEntity<?> getCartById(@PathVariable Long cartId){
        CartDTO cart = cartService.getCartById(cartId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<?> getCartsByUser(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO carts = cartService.getCartByUserId(emailId, cartId);
        return ResponseEntity.ok(carts);
    }
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @PathVariable String operation) {
        CartDTO cart = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1: 1);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }
    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String removingCart = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(removingCart, HttpStatus.OK);
    }


}
