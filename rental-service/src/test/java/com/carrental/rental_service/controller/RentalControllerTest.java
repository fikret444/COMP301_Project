package com.carrental.rental_service.controller;

import com.carrental.rental_service.dto.RentalRequest;
import com.carrental.rental_service.dto.RentalResponse;
import com.carrental.rental_service.model.RentalStatus;
import com.carrental.rental_service.service.RentalService;
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
import java.time.LocalDate;
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
 * RentalController Integration Test
 */
@WebMvcTest(RentalController.class)
@DisplayName("Rental Controller Tests")
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    private ObjectMapper objectMapper;
    private RentalRequest rentalRequest;
    private RentalResponse rentalResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        rentalRequest = new RentalRequest();
        rentalRequest.setCarId("car123");
        rentalRequest.setUserId(1L);
        rentalRequest.setStartDate(LocalDate.now());
        rentalRequest.setEndDate(LocalDate.now().plusDays(5));
        rentalRequest.setPaymentMethod("CREDIT_CARD");
        rentalRequest.setNotes("Test rental");

        rentalResponse = new RentalResponse();
        rentalResponse.setId(1L);
        rentalResponse.setCarId("car123");
        rentalResponse.setUserId(1L);
        rentalResponse.setStartDate(LocalDate.now());
        rentalResponse.setEndDate(LocalDate.now().plusDays(5));
        rentalResponse.setTotalPrice(new BigDecimal("3750.00"));
        rentalResponse.setStatus(RentalStatus.CONFIRMED);
        rentalResponse.setCreatedAt(LocalDateTime.now());
    }

    // ==================== CREATE RENTAL TESTS ====================

    @Test
    @DisplayName("POST /api/rentals - Kiralama oluşturulmalı ve 201 dönmeli")
    void whenCreateRental_thenReturn201() throws Exception {
        // Given
        when(rentalService.createRental(any(RentalRequest.class))).thenReturn(rentalResponse);

        // When & Then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.carId").value("car123"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(rentalService, times(1)).createRental(any(RentalRequest.class));
    }

    @Test
    @DisplayName("POST /api/rentals - Hata durumunda 400 dönmeli")
    void whenCreateRentalFails_thenReturn400() throws Exception {
        // Given
        when(rentalService.createRental(any(RentalRequest.class)))
                .thenThrow(new RuntimeException("Car not found"));

        // When & Then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(rentalService, times(1)).createRental(any(RentalRequest.class));
    }

    // ==================== GET RENTAL BY ID TESTS ====================

    @Test
    @DisplayName("GET /api/rentals/{id} - Kiralama bulunmalı")
    void whenGetRentalById_thenReturnRental() throws Exception {
        // Given
        when(rentalService.getRentalById(1L)).thenReturn(rentalResponse);

        // When & Then
        mockMvc.perform(get("/api/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.carId").value("car123"));

        verify(rentalService, times(1)).getRentalById(1L);
    }

    @Test
    @DisplayName("GET /api/rentals/{id} - Bulunamayan kiralama için 404")
    void whenGetNonExistingRental_thenReturn404() throws Exception {
        // Given
        when(rentalService.getRentalById(anyLong()))
                .thenThrow(new RuntimeException("Rental not found"));

        // When & Then
        mockMvc.perform(get("/api/rentals/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(rentalService, times(1)).getRentalById(999L);
    }

    // ==================== GET RENTALS BY USER ID TESTS ====================

    @Test
    @DisplayName("GET /api/rentals/user/{userId} - Kullanıcının kiralamaları listelenmeli")
    void whenGetRentalsByUserId_thenReturnRentals() throws Exception {
        // Given
        List<RentalResponse> rentals = Arrays.asList(rentalResponse);
        when(rentalService.getRentalsByUserId(1L)).thenReturn(rentals);

        // When & Then
        mockMvc.perform(get("/api/rentals/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(rentalService, times(1)).getRentalsByUserId(1L);
    }

    // ==================== GET RENTALS BY CAR ID TESTS ====================

    @Test
    @DisplayName("GET /api/rentals/car/{carId} - Aracın kiralamaları listelenmeli")
    void whenGetRentalsByCarId_thenReturnRentals() throws Exception {
        // Given
        List<RentalResponse> rentals = Arrays.asList(rentalResponse);
        when(rentalService.getRentalsByCarId("car123")).thenReturn(rentals);

        // When & Then
        mockMvc.perform(get("/api/rentals/car/car123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].carId").value("car123"));

        verify(rentalService, times(1)).getRentalsByCarId("car123");
    }

    // ==================== GET ALL RENTALS TESTS ====================

    @Test
    @DisplayName("GET /api/rentals - Tüm kiralamalar listelenmeli")
    void whenGetAllRentals_thenReturnAllRentals() throws Exception {
        // Given
        List<RentalResponse> rentals = Arrays.asList(rentalResponse);
        when(rentalService.getAllRentals()).thenReturn(rentals);

        // When & Then
        mockMvc.perform(get("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(rentalService, times(1)).getAllRentals();
    }

    // ==================== UPDATE STATUS TESTS ====================

    @Test
    @DisplayName("PATCH /api/rentals/{id}/status - Durum güncellenebilmeli")
    void whenUpdateRentalStatus_thenReturnUpdatedRental() throws Exception {
        // Given
        rentalResponse.setStatus(RentalStatus.ACTIVE);
        when(rentalService.updateRentalStatus(1L, RentalStatus.ACTIVE)).thenReturn(rentalResponse);

        // When & Then
        mockMvc.perform(patch("/api/rentals/1/status")
                        .param("status", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(rentalService, times(1)).updateRentalStatus(1L, RentalStatus.ACTIVE);
    }

    @Test
    @DisplayName("PATCH /api/rentals/{id}/status - Hata durumunda 400")
    void whenUpdateStatusFails_thenReturn400() throws Exception {
        // Given
        when(rentalService.updateRentalStatus(anyLong(), any(RentalStatus.class)))
                .thenThrow(new RuntimeException("Update failed"));

        // When & Then
        mockMvc.perform(patch("/api/rentals/1/status")
                        .param("status", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ==================== CANCEL RENTAL TESTS ====================

    @Test
    @DisplayName("DELETE /api/rentals/{id} - Kiralama iptal edilmeli ve 204 dönmeli")
    void whenCancelRental_thenReturn204() throws Exception {
        // Given
        doNothing().when(rentalService).cancelRental(1L);

        // When & Then
        mockMvc.perform(delete("/api/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(rentalService, times(1)).cancelRental(1L);
    }

    @Test
    @DisplayName("DELETE /api/rentals/{id} - İptal edilemeyen kiralama için 400")
    void whenCancelRentalFails_thenReturn400() throws Exception {
        // Given
        doThrow(new RuntimeException("Cannot cancel active rental"))
                .when(rentalService).cancelRental(1L);

        // When & Then
        mockMvc.perform(delete("/api/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(rentalService, times(1)).cancelRental(1L);
    }
}

