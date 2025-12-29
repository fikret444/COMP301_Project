package com.carrental.user_service.dto;

import lombok.Data;

@Data // Getter, Setter metodlarını otomatik oluşturur
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}