package com.carrental.user_service.repository;

import com.carrental.user_service.model.Role;
import com.carrental.user_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository Integration Test
 * @DataJpaTest: H2 in-memory database kullanarak repository testlerini yapar
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Her testten önce temiz bir kullanıcı oluştur
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("Kullanıcı başarıyla kaydedilmeli")
    void whenSaveUser_thenUserIsSaved() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Username ile kullanıcı bulunmalı")
    void whenFindByUsername_thenReturnUser() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Olmayan username ile arama yapılınca Optional.empty dönmeli")
    void whenFindByNonExistingUsername_thenReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistinguser");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Kullanıcı başarıyla silinmeli")
    void whenDeleteUser_thenUserIsDeleted() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        userRepository.delete(savedUser);

        // Then
        Optional<User> deleted = userRepository.findById(savedUser.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Birden fazla kullanıcı kaydedilebilmeli")
    void whenSaveMultipleUsers_thenAllUsersAreSaved() {
        // Given
        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");
        user2.setPassword("password2");
        user2.setRole(Role.ADMIN);

        // When
        userRepository.save(testUser);
        userRepository.save(user2);

        // Then
        assertThat(userRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Kullanıcı bilgileri güncellenebilmeli")
    void whenUpdateUser_thenUserIsUpdated() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        savedUser.setEmail("newemail@example.com");
        savedUser.setRole(Role.ADMIN);
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
        assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);
    }
}

