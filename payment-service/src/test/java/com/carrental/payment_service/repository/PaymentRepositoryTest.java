package com.carrental.payment_service.repository;

import com.carrental.payment_service.model.Payment;
import com.carrental.payment_service.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PaymentRepository Integration Test
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Payment Repository Tests")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        
        testPayment = new Payment();
        testPayment.setRentalId(1L);
        testPayment.setUserId(1L);
        testPayment.setAmount(new BigDecimal("3750.00"));
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setPaymentMethod("CREDIT_CARD");
        testPayment.setTransactionId("TXN12345");
        testPayment.setPaymentDate(LocalDateTime.now());
        testPayment.setDescription("Test payment");
    }

    @Test
    @DisplayName("Ödeme başarıyla kaydedilmeli")
    void whenSavePayment_thenPaymentIsSaved() {
        // When
        Payment savedPayment = paymentRepository.save(testPayment);

        // Then
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getId()).isNotNull();
        assertThat(savedPayment.getRentalId()).isEqualTo(1L);
        assertThat(savedPayment.getUserId()).isEqualTo(1L);
        assertThat(savedPayment.getAmount()).isEqualByComparingTo(new BigDecimal("3750.00"));
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("User ID'ye göre ödemeler bulunmalı")
    void whenFindByUserId_thenReturnPayments() {
        // Given
        paymentRepository.save(testPayment);
        
        Payment payment2 = new Payment();
        payment2.setRentalId(2L);
        payment2.setUserId(1L);
        payment2.setAmount(new BigDecimal("4250.00"));
        payment2.setStatus(PaymentStatus.PENDING);
        payment2.setPaymentMethod("DEBIT_CARD");
        payment2.setTransactionId("TXN67890");
        payment2.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment2);

        // When
        List<Payment> payments = paymentRepository.findByUserId(1L);

        // Then
        assertThat(payments).hasSize(2);
        assertThat(payments).extracting(Payment::getUserId).containsOnly(1L);
    }

    @Test
    @DisplayName("Rental ID'ye göre ödeme bulunmalı")
    void whenFindByRentalId_thenReturnPayment() {
        // Given
        paymentRepository.save(testPayment);

        // When
        Optional<Payment> found = paymentRepository.findByRentalId(1L);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRentalId()).isEqualTo(1L);
        assertThat(found.get().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Olmayan rental ID ile arama yapılınca boş Optional dönmeli")
    void whenFindByNonExistingRentalId_thenReturnEmpty() {
        // When
        Optional<Payment> found = paymentRepository.findByRentalId(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("ID ile ödeme bulunmalı")
    void whenFindById_thenReturnPayment() {
        // Given
        Payment savedPayment = paymentRepository.save(testPayment);

        // When
        Optional<Payment> found = paymentRepository.findById(savedPayment.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTransactionId()).isEqualTo("TXN12345");
    }

    @Test
    @DisplayName("Ödeme durumu güncellenebilmeli")
    void whenUpdatePaymentStatus_thenStatusIsUpdated() {
        // Given
        Payment savedPayment = paymentRepository.save(testPayment);

        // When
        savedPayment.setStatus(PaymentStatus.REFUNDED);
        Payment updatedPayment = paymentRepository.save(savedPayment);

        // Then
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    @DisplayName("Tüm ödemeler listelenebilmeli")
    void whenFindAll_thenReturnAllPayments() {
        // Given
        paymentRepository.save(testPayment);
        
        Payment payment2 = new Payment();
        payment2.setRentalId(2L);
        payment2.setUserId(2L);
        payment2.setAmount(new BigDecimal("5000.00"));
        payment2.setStatus(PaymentStatus.COMPLETED);
        payment2.setPaymentMethod("CASH");
        payment2.setTransactionId("TXN11111");
        payment2.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment2);

        // When
        List<Payment> payments = paymentRepository.findAll();

        // Then
        assertThat(payments).hasSize(2);
    }

    @Test
    @DisplayName("Ödeme silinebilmeli")
    void whenDeletePayment_thenPaymentIsDeleted() {
        // Given
        Payment savedPayment = paymentRepository.save(testPayment);

        // When
        paymentRepository.deleteById(savedPayment.getId());

        // Then
        Optional<Payment> deleted = paymentRepository.findById(savedPayment.getId());
        assertThat(deleted).isEmpty();
    }
}

