package com.carrental.payment_service.controller;

import com.carrental.payment_service.dto.PaymentRequest;
import com.carrental.payment_service.dto.PaymentResponse;
import com.carrental.payment_service.model.PaymentStatus;
import com.carrental.payment_service.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PaymentController Integration Test
 */
@WebMvcTest(PaymentController.class)
@DisplayName("Payment Controller Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private ObjectMapper objectMapper;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        paymentRequest = new PaymentRequest();
        paymentRequest.setRentalId(1L);
        paymentRequest.setUserId(1L);
        paymentRequest.setAmount(new BigDecimal("3750.00"));
        paymentRequest.setPaymentMethod("CREDIT_CARD");
        paymentRequest.setDescription("Test payment");

        paymentResponse = new PaymentResponse();
        paymentResponse.setId(1L);
        paymentResponse.setRentalId(1L);
        paymentResponse.setUserId(1L);
        paymentResponse.setAmount(new BigDecimal("3750.00"));
        paymentResponse.setStatus(PaymentStatus.COMPLETED);
        paymentResponse.setPaymentMethod("CREDIT_CARD");
        paymentResponse.setTransactionId("TXN12345");
        paymentResponse.setPaymentDate(LocalDateTime.now());
        paymentResponse.setDescription("Test payment");
    }

    // ==================== PROCESS PAYMENT TESTS ====================

    @Test
    @DisplayName("POST /api/payments/process - Ödeme işlenmeli ve 201 dönmeli")
    void whenProcessPayment_thenReturn201() throws Exception {
        // Given
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rentalId").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").value("TXN12345"));

        verify(paymentService, times(1)).processPayment(any(PaymentRequest.class));
    }

    @Test
    @DisplayName("POST /api/payments/process - Hata durumunda 400 dönmeli")
    void whenProcessPaymentFails_thenReturn400() throws Exception {
        // Given
        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Payment processing failed"));

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(paymentService, times(1)).processPayment(any(PaymentRequest.class));
    }

    // ==================== GET PAYMENT BY ID TESTS ====================

    @Test
    @DisplayName("GET /api/payments/{id} - Ödeme bulunmalı")
    void whenGetPaymentById_thenReturnPayment() throws Exception {
        // Given
        when(paymentService.getPaymentById(1L)).thenReturn(paymentResponse);

        // When & Then
        mockMvc.perform(get("/api/payments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.transactionId").value("TXN12345"));

        verify(paymentService, times(1)).getPaymentById(1L);
    }

    @Test
    @DisplayName("GET /api/payments/{id} - Bulunamayan ödeme için 404")
    void whenGetNonExistingPayment_thenReturn404() throws Exception {
        // Given
        when(paymentService.getPaymentById(anyLong()))
                .thenThrow(new RuntimeException("Payment not found"));

        // When & Then
        mockMvc.perform(get("/api/payments/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(paymentService, times(1)).getPaymentById(999L);
    }

    // ==================== GET PAYMENTS BY USER ID TESTS ====================

    @Test
    @DisplayName("GET /api/payments/user/{userId} - Kullanıcının ödemeleri listelenmeli")
    void whenGetPaymentsByUserId_thenReturnPayments() throws Exception {
        // Given
        List<PaymentResponse> payments = Arrays.asList(paymentResponse);
        when(paymentService.getPaymentsByUserId(1L)).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(paymentService, times(1)).getPaymentsByUserId(1L);
    }

    // ==================== GET PAYMENT BY RENTAL ID TESTS ====================

    @Test
    @DisplayName("GET /api/payments/rental/{rentalId} - Kiralama için ödeme bulunmalı")
    void whenGetPaymentByRentalId_thenReturnPayment() throws Exception {
        // Given
        when(paymentService.getPaymentByRentalId(1L)).thenReturn(paymentResponse);

        // When & Then
        mockMvc.perform(get("/api/payments/rental/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalId").value(1));

        verify(paymentService, times(1)).getPaymentByRentalId(1L);
    }

    @Test
    @DisplayName("GET /api/payments/rental/{rentalId} - Bulunamayan kiralama için 404")
    void whenGetPaymentByNonExistingRentalId_thenReturn404() throws Exception {
        // Given
        when(paymentService.getPaymentByRentalId(anyLong()))
                .thenThrow(new RuntimeException("Payment not found"));

        // When & Then
        mockMvc.perform(get("/api/payments/rental/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(paymentService, times(1)).getPaymentByRentalId(999L);
    }

    // ==================== GET ALL PAYMENTS TESTS ====================

    @Test
    @DisplayName("GET /api/payments - Tüm ödemeler listelenmeli")
    void whenGetAllPayments_thenReturnAllPayments() throws Exception {
        // Given
        List<PaymentResponse> payments = Arrays.asList(paymentResponse);
        when(paymentService.getAllPayments()).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(paymentService, times(1)).getAllPayments();
    }

    // ==================== REFUND PAYMENT TESTS ====================

    @Test
    @DisplayName("POST /api/payments/{paymentId}/refund - Ödeme iade edilmeli")
    void whenRefundPayment_thenReturnRefundedPayment() throws Exception {
        // Given
        paymentResponse.setStatus(PaymentStatus.REFUNDED);
        when(paymentService.refundPayment(1L)).thenReturn(paymentResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/1/refund")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        verify(paymentService, times(1)).refundPayment(1L);
    }

    @Test
    @DisplayName("POST /api/payments/{paymentId}/refund - İade edilemeyen ödeme için 400")
    void whenRefundPaymentFails_thenReturn400() throws Exception {
        // Given
        when(paymentService.refundPayment(anyLong()))
                .thenThrow(new RuntimeException("Only completed payments can be refunded"));

        // When & Then
        mockMvc.perform(post("/api/payments/1/refund")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(paymentService, times(1)).refundPayment(1L);
    }

    @Test
    @DisplayName("POST /api/payments/process - Geçersiz JSON ile 400")
    void whenProcessPaymentWithInvalidJson_thenReturn400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).processPayment(any(PaymentRequest.class));
    }
}

