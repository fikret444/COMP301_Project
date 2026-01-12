package com.carrental.payment_service.dto;

import com.carrental.payment_service.model.PaymentStatus;
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
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String description;
}

