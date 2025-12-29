package com.carrental.user_service.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Bu anahtar Token'ın imzasıdır. Kimse bu anahtarı bilmemeli!
    // Gerçek projede bunu gizli bir dosyadan okuruz, şimdilik buraya uzun rastgele bir şey yazıyoruz.
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName) // Token kime ait?
                .setIssuedAt(new Date(System.currentTimeMillis())) // Ne zaman üretildi?
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 Dakika sonra geçersiz olsun
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // İmzalama algoritması
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}