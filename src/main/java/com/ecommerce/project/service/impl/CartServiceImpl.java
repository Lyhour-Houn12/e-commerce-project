package com.ecommerce.project.service.impl;

import com.ecommerce.project.entity.Cart;
import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartItemDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository  cartItemRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // Find existing cart or create one
        Cart cart = createCart();
        // Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        // Perform Validation
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if(cartItem != null){
            throw new APIException("Product" + product.getProductName() + " already exists");
        }
        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is not available");
        }
        if(product.getQuantity() < quantity){
            throw new APIException("Please, make of order of the " +product.getProductName() + " less than or equal to the quantity" + product.getQuantity());
        }
        // Save Cart Item
        // Create Cart Item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);
        // reduce stock in product
        product.setQuantity(product.getQuantity());

        // total price in cart
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        // add item to card list
        cart.getCartItems().add(newCartItem);

        cartRepository.save(cart);
        // Return updated cart
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> products = cartItems.stream()
                .map(item -> {
                   ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
                   map.setQuantity(item.getQuantity());
                   return map;
                })
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Override
    public List<CartDTO> getCarts() {
        List<Cart> carts =  cartRepository.findAll();
        if(carts.isEmpty()){
            throw new APIException("There is no existing cart.");
        }
        return carts.stream().map(this::getCartDTO).toList();
    }
    @Override
    public CartDTO getCartByUserId(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId)
                .orElseThrow(() ->  new ResourceNotFoundException("Email" + emailId + " And " + "Cart", "id", cartId));

        return getCartDTO(cart);
    }
    @Override
    public CartDTO getCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        return getCartDTO(cart);
    }


    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart cartUser =  cartRepository.findCartByEmail(emailId);
        Long cartId = cartUser.getCartId();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is not available");
        }
        if(product.getQuantity() < quantity){
            throw new APIException("Please, make of order of the " +product.getProductName() + " less than or equal to the quantity" + product.getQuantity());
        }
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null){
            throw new APIException("Product " + product.getProductName() + " does not exist");
        }

        int newQuantity = cartItem.getQuantity() + quantity;
        if(newQuantity < 0){
            throw new APIException("The resulting quantity cannot be negative");
        }

        cartItem.setQuantity(cartItem.getQuantity() +  quantity);
        cartItem.setDiscount(product.getDiscount());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

        cartRepository.save(cart);
        CartItem updateCartItem = cartItemRepository.save(cartItem);
        if(updateCartItem.getQuantity() == 0){
            cartItemRepository.deleteById(updateCartItem.getCartItemId());
        }
        return getCartDTO(cart);
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem == null){
            throw new APIException("Product " + productId + " does not exist");
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartRepository.save(cart);
        cartItemRepository.deleteCartItemByProductIdAndCartId(productId, cartId);

        return "Product with ID " + productId + " has been removed from the cart.";
    }

    @NonNull
    private CartDTO getCartDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTO = cart.getCartItems().stream()
                .map(item ->{
                    ProductDTO products =  modelMapper.map(item.getProduct(), ProductDTO.class);
                    products.setQuantity(item.getQuantity());
                    return products;
                })
                .toList();
        cartDTO.setProducts(productDTO);
        return cartDTO;
    }

    @Override
    public void updateProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null){
            throw new APIException("Product " + product.getProductName() + " does not exist");
        }
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartRepository.save(cart);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public String createOrUpdateCartWithItem(List<CartItemDTO> cartItems) {
        String emailId = authUtil.loggedInEmail();
        Double totalPrice = 0.00;
        Cart existingCart  = cartRepository.findCartByEmail(emailId);
        if(existingCart == null){
            existingCart = new Cart();
            existingCart.setUser(authUtil.loggedUser());
            existingCart.setTotalPrice(0.00);
            cartRepository.save(existingCart);
        }else{
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        for(CartItemDTO cartItemDTO : cartItems){
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

            // directly update product stock and totalPrice
            product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() *  quantity;

            // Create and save the cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());

            cartItemRepository.save(cartItem);
        }
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);

        return "Cart created or deleted with new items successfully";
    }

    private Cart createCart(){
        Cart cartUser = cartRepository.findCartByEmail((authUtil.loggedInEmail()));
        if(cartUser != null){
            return cartUser;
        }
        Cart newCart = new Cart();
        newCart.setUser(authUtil.loggedUser());
        newCart.setTotalPrice(0.00);
        return cartRepository.save(newCart);
    }
}
