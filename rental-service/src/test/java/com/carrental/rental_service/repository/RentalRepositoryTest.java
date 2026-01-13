package com.carrental.rental_service.repository;

import com.carrental.rental_service.model.Rental;
import com.carrental.rental_service.model.RentalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RentalRepository Integration Test
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Rental Repository Tests")
class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;

    private Rental testRental;

    @BeforeEach
    void setUp() {
        rentalRepository.deleteAll();
        
        testRental = new Rental();
        testRental.setCarId("car123");
        testRental.setUserId(1L);
        testRental.setStartDate(LocalDate.now());
        testRental.setEndDate(LocalDate.now().plusDays(5));
        testRental.setTotalPrice(new BigDecimal("3750.00"));
        testRental.setStatus(RentalStatus.PENDING);
        testRental.setNotes("Test rental");
    }

    @Test
    @DisplayName("Kiralama başarıyla kaydedilmeli")
    void whenSaveRental_thenRentalIsSaved() {
        // When
        Rental savedRental = rentalRepository.save(testRental);

        // Then
        assertThat(savedRental).isNotNull();
        assertThat(savedRental.getId()).isNotNull();
        assertThat(savedRental.getCarId()).isEqualTo("car123");
        assertThat(savedRental.getUserId()).isEqualTo(1L);
        assertThat(savedRental.getStatus()).isEqualTo(RentalStatus.PENDING);
    }

    @Test
    @DisplayName("User ID'ye göre kiralamalar bulunmalı")
    void whenFindByUserId_thenReturnRentals() {
        // Given
        rentalRepository.save(testRental);
        
        Rental rental2 = new Rental();
        rental2.setCarId("car456");
        rental2.setUserId(1L);
        rental2.setStartDate(LocalDate.now().plusDays(10));
        rental2.setEndDate(LocalDate.now().plusDays(15));
        rental2.setTotalPrice(new BigDecimal("4250.00"));
        rental2.setStatus(RentalStatus.CONFIRMED);
        rentalRepository.save(rental2);

        // When
        List<Rental> rentals = rentalRepository.findByUserId(1L);

        // Then
        assertThat(rentals).hasSize(2);
        assertThat(rentals).extracting(Rental::getUserId).containsOnly(1L);
    }

    @Test
    @DisplayName("Car ID'ye göre kiralamalar bulunmalı")
    void whenFindByCarId_thenReturnRentals() {
        // Given
        rentalRepository.save(testRental);
        
        Rental rental2 = new Rental();
        rental2.setCarId("car123");
        rental2.setUserId(2L);
        rental2.setStartDate(LocalDate.now().plusDays(7));
        rental2.setEndDate(LocalDate.now().plusDays(10));
        rental2.setTotalPrice(new BigDecimal("2250.00"));
        rental2.setStatus(RentalStatus.ACTIVE);
        rentalRepository.save(rental2);

        // When
        List<Rental> rentals = rentalRepository.findByCarId("car123");

        // Then
        assertThat(rentals).hasSize(2);
        assertThat(rentals).extracting(Rental::getCarId).containsOnly("car123");
    }

    @Test
    @DisplayName("ID ile kiralama bulunmalı")
    void whenFindById_thenReturnRental() {
        // Given
        Rental savedRental = rentalRepository.save(testRental);

        // When
        Optional<Rental> found = rentalRepository.findById(savedRental.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCarId()).isEqualTo("car123");
        assertThat(found.get().getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Kiralama durumu güncellenebilmeli")
    void whenUpdateRentalStatus_thenStatusIsUpdated() {
        // Given
        Rental savedRental = rentalRepository.save(testRental);

        // When
        savedRental.setStatus(RentalStatus.CONFIRMED);
        Rental updatedRental = rentalRepository.save(savedRental);

        // Then
        assertThat(updatedRental.getStatus()).isEqualTo(RentalStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Kiralama silinebilmeli")
    void whenDeleteRental_thenRentalIsDeleted() {
        // Given
        Rental savedRental = rentalRepository.save(testRental);

        // When
        rentalRepository.deleteById(savedRental.getId());

        // Then
        Optional<Rental> deleted = rentalRepository.findById(savedRental.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("PrePersist annotation createdAt alanını set etmeli")
    void whenSaveRental_thenCreatedAtIsSet() {
        // When
        Rental savedRental = rentalRepository.save(testRental);

        // Then
        assertThat(savedRental.getCreatedAt()).isNotNull();
    }
}

