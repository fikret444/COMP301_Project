package com.carrental.rental_service.dto;

import com.carrental.rental_service.model.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalResponse {
    private Long id;
    private String carId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private RentalStatus status;
    private LocalDateTime createdAt;
    private String notes;
    private PaymentResponse payment;
}

