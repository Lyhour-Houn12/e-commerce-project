package com.ecommerce.project.service.impl;

import com.ecommerce.project.payload.AnalyticResponse;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    @Override
    public AnalyticResponse getAnalyticData() {
        AnalyticResponse response = new AnalyticResponse();
        Long productCount = productRepository.count();
        Long orderCount = orderRepository.count();
        Double totalRevenue = orderRepository.getTotalRevenue();
        response.setProductCount(String.valueOf(productCount));
        response.setTotalOrder(String.valueOf(orderCount));
        response.setTotalRevenue(String.valueOf(totalRevenue != null ? totalRevenue : 0));
        return response;
    }
}
