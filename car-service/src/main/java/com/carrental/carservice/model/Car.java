package com.carrental.carservice.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@Document(collection = "cars")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    private String id;
    
    @NotBlank(message = "Marka boş olamaz")
    private String brand;
    
    @NotBlank(message = "Model boş olamaz")
    private String model;
    
    @Min(value = 1900, message = "Yıl 1900'den küçük olamaz")
    private int year;
    
    @NotNull(message = "Günlük fiyat boş olamaz")
    @DecimalMin(value = "0.01", message = "Günlük fiyat 0'dan büyük olmalıdır")
    @JsonProperty("pricePerDay")
    private BigDecimal dailyPrice;
    
    private boolean isAvailable;
}