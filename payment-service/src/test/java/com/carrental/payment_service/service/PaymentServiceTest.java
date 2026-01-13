package com.carrental.payment_service.service;

import com.carrental.payment_service.dto.PaymentRequest;
import com.carrental.payment_service.dto.PaymentResponse;
import com.carrental.payment_service.model.Payment;
import com.carrental.payment_service.model.PaymentStatus;
import com.carrental.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PaymentService Unit Test
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setRentalId(1L);
        paymentRequest.setUserId(1L);
        paymentRequest.setAmount(new BigDecimal("3750.00"));
        paymentRequest.setPaymentMethod("CREDIT_CARD");
        paymentRequest.setDescription("Test payment");

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setRentalId(1L);
        testPayment.setUserId(1L);
        testPayment.setAmount(new BigDecimal("3750.00"));
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setPaymentMethod("CREDIT_CARD");
        testPayment.setTransactionId("TXN12345");
        testPayment.setPaymentDate(LocalDateTime.now());
        testPayment.setDescription("Test payment");
    }

    // ==================== PROCESS PAYMENT TESTS ====================

    @Test
    @DisplayName("Ödeme başarıyla işlenmeli")
    void whenProcessPayment_thenSuccess() {
        // Given
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.processPayment(paymentRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRentalId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("3750.00"));
        assertThat(response.getTransactionId()).isNotNull();
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Ödeme işlenirken transaction ID oluşturulmalı")
    void whenProcessPayment_thenTransactionIdGenerated() {
        // Given
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            assertThat(payment.getTransactionId()).isNotNull();
            return payment;
        });

        // When
        paymentService.processPayment(paymentRequest);

        // Then
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Ödeme işlenirken payment date set edilmeli")
    void whenProcessPayment_thenPaymentDateSet() {
        // Given
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            assertThat(payment.getPaymentDate()).isNotNull();
            return payment;
        });

        // When
        paymentService.processPayment(paymentRequest);

        // Then
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    // ==================== GET PAYMENT TESTS ====================

    @Test
    @DisplayName("ID ile ödeme bulunmalı")
    void whenGetPaymentById_thenReturnPayment() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When
        PaymentResponse response = paymentService.getPaymentById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTransactionId()).isEqualTo("TXN12345");
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Olmayan ID ile arama yapılınca hata vermeli")
    void whenGetPaymentByNonExistingId_thenThrowException() {
        // Given
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.getPaymentById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(paymentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("User ID'ye göre ödemeler listelenmeli")
    void whenGetPaymentsByUserId_thenReturnPayments() {
        // Given
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findByUserId(1L)).thenReturn(payments);

        // When
        List<PaymentResponse> responses = paymentService.getPaymentsByUserId(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(1L);
        verify(paymentRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Rental ID'ye göre ödeme bulunmalı")
    void whenGetPaymentByRentalId_thenReturnPayment() {
        // Given
        when(paymentRepository.findByRentalId(1L)).thenReturn(Optional.of(testPayment));

        // When
        PaymentResponse response = paymentService.getPaymentByRentalId(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRentalId()).isEqualTo(1L);
        verify(paymentRepository, times(1)).findByRentalId(1L);
    }

    @Test
    @DisplayName("Olmayan rental ID ile arama yapılınca hata vermeli")
    void whenGetPaymentByNonExistingRentalId_thenThrowException() {
        // Given
        when(paymentRepository.findByRentalId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.getPaymentByRentalId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(paymentRepository, times(1)).findByRentalId(999L);
    }

    @Test
    @DisplayName("Tüm ödemeler listelenmeli")
    void whenGetAllPayments_thenReturnAllPayments() {
        // Given
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findAll()).thenReturn(payments);

        // When
        List<PaymentResponse> responses = paymentService.getAllPayments();

        // Then
        assertThat(responses).hasSize(1);
        verify(paymentRepository, times(1)).findAll();
    }

    // ==================== REFUND PAYMENT TESTS ====================

    @Test
    @DisplayName("Completed ödeme iade edilebilmeli")
    void whenRefundCompletedPayment_thenSuccess() {
        // Given
        testPayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.refundPayment(1L);

        // Then
        assertThat(response).isNotNull();
        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Pending ödeme iade edilemez")
    void whenRefundPendingPayment_thenThrowException() {
        // Given
        testPayment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When & Then
        assertThatThrownBy(() -> paymentService.refundPayment(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only completed payments can be refunded");

        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Olmayan ödeme iade edilmeye çalışılınca hata vermeli")
    void whenRefundNonExistingPayment_thenThrowException() {
        // Given
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.refundPayment(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(paymentRepository, times(1)).findById(999L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("İade işleminde status REFUNDED olmalı")
    void whenRefundPayment_thenStatusShouldBeRefunded() {
        // Given
        testPayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
            return payment;
        });

        // When
        paymentService.refundPayment(1L);

        // Then
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}

