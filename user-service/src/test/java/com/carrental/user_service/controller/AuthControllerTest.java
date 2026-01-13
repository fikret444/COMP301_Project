package com.carrental.user_service.controller;

import com.carrental.user_service.dto.LoginRequest;
import com.carrental.user_service.dto.RegisterRequest;
import com.carrental.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController Integration Test
 * @WebMvcTest: Sadece controller katmanını test eder, Spring Security dahil
 */
@WebMvcTest(AuthController.class)
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Password123!");
    }

    // ==================== REGISTER ENDPOINT TESTS ====================

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - Başarılı kayıt")
    void whenRegisterWithValidData_thenReturn200() throws Exception {
        // Given
        when(userService.registerUser(any(RegisterRequest.class)))
                .thenReturn("Kullanıcı başarıyla oluşturuldu: testuser");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Kullanıcı başarıyla oluşturuldu: testuser"));

        verify(userService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - Zaten var olan kullanıcı ile hata")
    void whenRegisterWithExistingUsername_thenReturn4xx() throws Exception {
        // Given
        when(userService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Bu kullanıcı adı zaten kullanılıyor!"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(userService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register - Geçersiz JSON formatı ile 400")
    void whenRegisterWithInvalidJson_thenReturn400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any(RegisterRequest.class));
    }

    // ==================== LOGIN ENDPOINT TESTS ====================

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - Başarılı login ile JWT token dönmeli")
    void whenLoginWithValidCredentials_thenReturnToken() throws Exception {
        // Given
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        when(userService.login(any(LoginRequest.class))).thenReturn(jwtToken);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(jwtToken));

        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - Yanlış şifre ile hata")
    void whenLoginWithWrongPassword_thenReturn4xx() throws Exception {
        // Given
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Şifre hatalı!"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - Olmayan kullanıcı ile hata")
    void whenLoginWithNonExistingUser_thenReturn4xx() throws Exception {
        // Given
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Kullanıcı bulunamadı!"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - Boş request body ile 400")
    void whenLoginWithEmptyBody_thenReturn400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login - Content-Type header eksik ise 415")
    void whenLoginWithoutContentType_thenReturn415() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}

