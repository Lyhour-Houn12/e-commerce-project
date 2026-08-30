package com.ecommerce.project.controller;


import com.ecommerce.project.payload.AnalyticResponse;
import com.ecommerce.project.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalyticController {
    private final AnalyticService analyticService;

    @GetMapping("/admin/app/analytics")
    public ResponseEntity<AnalyticResponse> getAnalytic(){
        AnalyticResponse response = analyticService.getAnalyticData();
        return ResponseEntity.ok(response);
    }
}
