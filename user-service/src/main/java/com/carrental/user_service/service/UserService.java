package com.carrental.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carrental.user_service.dto.LoginRequest;
import com.carrental.user_service.dto.RegisterRequest;
import com.carrental.user_service.model.Role;
import com.carrental.user_service.model.User;
import com.carrental.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // Token üretmek için bunu ekledik

    // KAYIT OLMA METODU
    public String registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return "Kullanıcı başarıyla oluşturuldu: " + user.getUsername();
    }

    // GİRİŞ YAPMA METODU (Hata veren eksik kısım burasıydı)
    public String login(LoginRequest request) {
        // 1. Kullanıcıyı bul
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        // 2. Şifreyi kontrol et
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Şifre hatalı!");
        }

        // 3. Token üret ve ver
        return jwtService.generateToken(user.getUsername());
    }
}