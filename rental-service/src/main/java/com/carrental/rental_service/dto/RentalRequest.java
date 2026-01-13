package com.carrental.rental_service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalRequest {
    @NotBlank(message = "Araba ID'si boş olamaz")
    private String carId;
    
    @NotNull(message = "Kullanıcı ID'si boş olamaz")
    private Long userId;
    
    @NotNull(message = "Başlangıç tarihi boş olamaz")
    @FutureOrPresent(message = "Başlangıç tarihi geçmiş olamaz")
    private LocalDate startDate;
    
    @NotNull(message = "Bitiş tarihi boş olamaz")
    @Future(message = "Bitiş tarihi gelecekte olmalıdır")
    private LocalDate endDate;
    
    @NotBlank(message = "Ödeme yöntemi boş olamaz")
    private String paymentMethod;
    
    private String notes;
}

