package com.carrental.rental_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    private String id;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private BigDecimal pricePerDay;
    private String status;
    private String category;
}

