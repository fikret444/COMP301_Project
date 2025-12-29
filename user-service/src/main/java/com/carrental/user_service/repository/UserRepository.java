package com.carrental.user_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrental.user_service.model.User;

// JpaRepository sayesinde "save", "findAll", "delete" gibi metodları bedavadan kazanıyoruz
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Giriş yaparken kullanıcı adına göre arama yapmak için özel metod
    Optional<User> findByUsername(String username);
}