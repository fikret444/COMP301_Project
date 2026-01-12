package com.carrental.rental_service.client;

import com.carrental.rental_service.dto.PaymentRequest;
import com.carrental.rental_service.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    
    @PostMapping("/api/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
}

