package com.carrental.payment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Kiralama ID'si boş olamaz")
    private Long rentalId;
    
    @NotNull(message = "Kullanıcı ID'si boş olamaz")
    private Long userId;
    
    @NotNull(message = "Tutar boş olamaz")
    @DecimalMin(value = "0.01", message = "Tutar 0'dan büyük olmalıdır")
    private BigDecimal amount;
    
    @NotBlank(message = "Ödeme yöntemi boş olamaz")
    private String paymentMethod;
    
    private String description;
}

