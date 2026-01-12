package com.carrental.carservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Document(collection = "cars")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    private String id;
    private String brand;
    private String model;
    private int year;
    private BigDecimal dailyPrice;
    private boolean isAvailable;
}