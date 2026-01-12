package com.carrental.rental_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long rentalId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String description;
}

