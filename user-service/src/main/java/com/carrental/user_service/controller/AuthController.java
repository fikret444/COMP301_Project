package com.carrental.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.user_service.dto.LoginRequest;
import com.carrental.user_service.dto.RegisterRequest;
import com.carrental.user_service.service.UserService;

import lombok.RequiredArgsConstructor; 



@RestController
@RequestMapping("/api/auth") // Adresimiz: http://localhost:8082/api/auth/...
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register") // .../register
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
     
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }  
}