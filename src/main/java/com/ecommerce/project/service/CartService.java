package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartItemDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getCarts();
    CartDTO getCartById(Long cartId);

    CartDTO getCartByUserId(String emailId, Long cartId);
    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    @Transactional
    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCart(Long cartId, Long productId);

    String createOrUpdateCartWithItem(List<CartItemDTO> cartItems);
}
