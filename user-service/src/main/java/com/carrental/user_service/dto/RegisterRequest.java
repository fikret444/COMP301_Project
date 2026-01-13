package com.carrental.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Getter, Setter metodlarını otomatik oluşturur
public class RegisterRequest {
    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;
    
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    @NotBlank(message = "E-posta boş olamaz")
    private String email;
    
    @NotBlank(message = "Şifre boş olamaz")
    private String password;
}