package com.carrental.user_service.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated; // Benzersiz ID için
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Bu sınıfın bir veritabanı tablosu olduğunu söyler
@Table(name = "users") // Tablonun adı 'users' olsun (PostgreSQL'de 'user' özel kelimedir)
@Data // Lombok: Getter, Setter, toString metodlarını otomatik yazar
@NoArgsConstructor // Parametresiz yapıcı metod (JPA için gerekli)
@AllArgsConstructor // Tüm parametreli yapıcı metod
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // ID'yi otomatik ve benzersiz (UUID) üret
    private UUID id;

    @Column(nullable = false, unique = true) // Kullanıcı adı boş olamaz ve benzersiz olmalı
    private String username;

    @Column(nullable = false)
    private String password; // Şifrelenmiş halde tutacağız

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING) // Enum'ı veritabanına String ("ADMIN", "USER") olarak kaydet
    private Role role;

}