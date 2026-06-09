package com.ecommerce.project.controller;

import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderResponseDTO;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.util.AuthUtil;
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


    @PostMapping("/order/users/payment/{paymentMethod}")
    public ResponseEntity<?> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderResponseDTO orderResponseDTO){
        String emailId = authUtil.loggedInEmail();
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
}
