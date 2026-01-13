package com.carrental.user_service.service;

import com.carrental.user_service.dto.LoginRequest;
import com.carrental.user_service.dto.RegisterRequest;
import com.carrental.user_service.model.Role;
import com.carrental.user_service.model.User;
import com.carrental.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService Unit Test
 * @ExtendWith(MockitoExtension.class): Mockito'yu JUnit 5 ile kullanmamızı sağlar
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRole(Role.USER);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("Password123!");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Password123!");
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("Yeni kullanıcı başarıyla kaydedilmeli")
    void whenRegisterNewUser_thenSuccess() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        String result = userService.registerUser(registerRequest);

        // Then
        assertThat(result).contains("başarıyla oluşturuldu");
        verify(userRepository, times(1)).findByUsername("newuser");
        verify(passwordEncoder, times(1)).encode("Password123!");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Zaten var olan username ile kayıt olunca hata vermeli")
    void whenRegisterWithExistingUsername_thenThrowException() {
        // Given
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("zaten kullanılıyor");

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Kayıt sırasında şifre encode edilmeli")
    void whenRegisterUser_thenPasswordShouldBeEncoded() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.getPassword()).isEqualTo("encodedPassword123");
            return savedUser;
        });

        // When
        userService.registerUser(registerRequest);

        // Then
        verify(passwordEncoder, times(1)).encode("Password123!");
    }

    @Test
    @DisplayName("Kayıt sırasında default role USER olmalı")
    void whenRegisterUser_thenRoleShouldBeUser() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.getRole()).isEqualTo(Role.USER);
            return savedUser;
        });

        // When
        userService.registerUser(registerRequest);

        // Then
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("Doğru credentials ile login başarılı olmalı")
    void whenLoginWithValidCredentials_thenReturnToken() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword123")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("jwt.token.here");

        // When
        String token = userService.login(loginRequest);

        // Then
        assertThat(token).isEqualTo("jwt.token.here");
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("Password123!", "encodedPassword123");
        verify(jwtService, times(1)).generateToken("testuser");
    }

    @Test
    @DisplayName("Olmayan kullanıcı ile login yapılınca hata vermeli")
    void whenLoginWithNonExistingUser_thenThrowException() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("bulunamadı");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Yanlış şifre ile login yapılınca hata vermeli")
    void whenLoginWithWrongPassword_thenThrowException() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword123")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("hatalı");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("Password123!", "encodedPassword123");
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Başarılı login sonrası JWT token oluşturulmalı")
    void whenLoginSuccessful_thenJwtTokenGenerated() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("generated.jwt.token");

        // When
        String token = userService.login(loginRequest);

        // Then
        assertThat(token).isNotEmpty();
        assertThat(token).isEqualTo("generated.jwt.token");
        verify(jwtService, times(1)).generateToken("testuser");
    }
}

