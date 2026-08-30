package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.*;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.service.StripePaymentService;
import com.ecommerce.project.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthUtil authUtil;
    private final StripePaymentService stripePaymentService;


    @PostMapping("/order/users/payment/{paymentMethod}")
    public ResponseEntity<?> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderResponseDTO orderResponseDTO){
        String emailId = authUtil.loggedInEmail();
        System.out.println("OrderController:orderProducts:" + orderResponseDTO);
        OrderDTO order = orderService.placeOrder(
                emailId,
                orderResponseDTO.getAddressId(),
                paymentMethod,
                orderResponseDTO.getPgName(),
                orderResponseDTO.getPgResponseMessage(),
                orderResponseDTO.getPgPaymentId(),
                orderResponseDTO.getPgStatus()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-payment-client")
    public ResponseEntity<String> stripePaymentClient(@RequestBody StripePaymentDto stripePaymentDto) throws StripeException {
        System.out.println("stripePaymentClient:" + stripePaymentDto);
        PaymentIntent paymentIntent = stripePaymentService.getPaymentIntent(stripePaymentDto);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }


    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }
    @GetMapping("/seller/orders")
    public ResponseEntity<OrderResponse> getAllSellerOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        OrderResponse orderResponse = orderService.getAllSellerOrders(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDTO status){
        OrderDTO order = orderService.updateOrder(orderId, status.getStatus());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/seller/orders/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatusSeller(@PathVariable Long orderId,
                                                            @RequestBody OrderStatusUpdateDTO orderStatusUpdateDto) {
        OrderDTO order = orderService.updateOrder(orderId, orderStatusUpdateDto.getStatus());
        return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);
    }


}
