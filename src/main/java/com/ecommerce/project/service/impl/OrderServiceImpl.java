package com.ecommerce.project.service.impl;

import com.ecommerce.project.entity.*;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderITemDTO;
import com.ecommerce.project.payload.OrderResponse;
import com.ecommerce.project.repository.*;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;

    @Transactional
    @Override
    public OrderDTO placeOrder(String emailId, Long addressId,
                               String paymentMethod, String pgName,
                               String pgResponseMessage, String pgPaymentId, String pgStatus) {
        // Getting user cart
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        // Create a new order with payment info
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod,pgPaymentId, pgStatus,pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        // Get items from the cart into the order items
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException("Cart is empty.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        orderItems = orderItemRepository.saveAll(orderItems);
        // update product stock
        // update product stock
        List<CartItem> itemsSnapshot = new ArrayList<>(cartItems);
        itemsSnapshot.forEach(item -> {
            System.out.println("cart.getCartId()=" + cart.getCartId() + ", productId=" + item.getProduct().getProductId());
            int quantity = item.getQuantity();
            Product product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProduct().getProductId()));
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });
        // send back the order summary
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(orderItem -> {
            orderDTO.getOrderItems().add(modelMapper.map(orderItem, OrderITemDTO.class));
        });
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }

    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        Page<Order> pageOrders = orderRepository.findAll(pageDetails);
        List<Order> orders = pageOrders.getContent();

        List<OrderDTO> orderDTOs = orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .toList();

        return OrderResponse.builder()
                .content(orderDTOs)
                .pageNumber(pageOrders.getNumber() + 1)
                .pageSize(pageOrders.getSize())
                .totalElements(pageOrders.getTotalElements())
                .totalPages(pageOrders.getTotalPages())
                .lastPage(pageOrders.isLast())
                .build();
    }

    @Override
    public OrderDTO updateOrder(Long orderId, String status) {
        Order order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setOrderStatus(status);
        orderRepository.save(order);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderResponse getAllSellerOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber - 1, pageSize, sortByAndOrder);
        User seller = authUtil.loggedUser();

        Page<Order> pageOrders = orderRepository.findAll(pageDetails);

        List<Order> sellerOrders = pageOrders.getContent().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(orderItem ->{
                            var product = orderItem.getProduct();
                            if(product == null || product.getUser() == null){
                                return false;
                            }
                            return product.getUser().getUserId().equals(seller.getUserId());
                        }))
                .toList();


        List<OrderDTO> orderDTOS = sellerOrders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .toList();

        return OrderResponse.builder()
                .content(orderDTOS)
                .pageNumber(pageOrders.getNumber() + 1)
                .pageSize(pageOrders.getSize())
                .totalElements(pageOrders.getTotalElements())
                .totalPages(pageOrders.getTotalPages())
                .lastPage(pageOrders.isLast())
                .build();
    }


}
