package com.carrental.rental_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalRequest {
    private String carId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String paymentMethod;
    private String notes;
}

