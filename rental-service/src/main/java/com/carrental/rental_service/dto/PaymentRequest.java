package com.carrental.rental_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long rentalId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String description;
}

