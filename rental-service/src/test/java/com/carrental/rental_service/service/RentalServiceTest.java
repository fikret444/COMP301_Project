package com.carrental.rental_service.service;

import com.carrental.rental_service.client.CarServiceClient;
import com.carrental.rental_service.client.PaymentServiceClient;
import com.carrental.rental_service.dto.*;
import com.carrental.rental_service.model.Rental;
import com.carrental.rental_service.model.RentalStatus;
import com.carrental.rental_service.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * RentalService Unit Test
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Rental Service Tests")
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarServiceClient carServiceClient;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private RentalService rentalService;

    private RentalRequest rentalRequest;
    private Rental testRental;
    private CarDto carDto;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        rentalRequest = new RentalRequest();
        rentalRequest.setCarId("car123");
        rentalRequest.setUserId(1L);
        rentalRequest.setStartDate(LocalDate.now());
        rentalRequest.setEndDate(LocalDate.now().plusDays(5));
        rentalRequest.setPaymentMethod("CREDIT_CARD");
        rentalRequest.setNotes("Test rental");

        testRental = new Rental();
        testRental.setId(1L);
        testRental.setCarId("car123");
        testRental.setUserId(1L);
        testRental.setStartDate(LocalDate.now());
        testRental.setEndDate(LocalDate.now().plusDays(5));
        testRental.setTotalPrice(new BigDecimal("3750.00"));
        testRental.setStatus(RentalStatus.PENDING);

        carDto = new CarDto();
        carDto.setId("car123");
        carDto.setBrand("BMW");
        carDto.setModel("320i");
        carDto.setPricePerDay(new BigDecimal("750.00"));

        paymentResponse = new PaymentResponse();
        paymentResponse.setId(1L);
        paymentResponse.setStatus("COMPLETED");
        paymentResponse.setAmount(new BigDecimal("3750.00"));
    }

    // ==================== CREATE RENTAL TESTS ====================

    @Test
    @DisplayName("Kiralama başarıyla oluşturulmalı")
    void whenCreateRental_thenSuccess() {
        // Given
        when(carServiceClient.getCarById("car123")).thenReturn(carDto);
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);
        when(paymentServiceClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // When
        RentalResponse response = rentalService.createRental(rentalRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCarId()).isEqualTo("car123");
        assertThat(response.getUserId()).isEqualTo(1L);
        verify(carServiceClient, times(1)).getCarById("car123");
        verify(rentalRepository, atLeast(1)).save(any(Rental.class));
        verify(paymentServiceClient, times(1)).processPayment(any(PaymentRequest.class));
    }

    @Test
    @DisplayName("Olmayan araç için kiralama oluşturulunca hata vermeli")
    void whenCreateRentalWithNonExistingCar_thenThrowException() {
        // Given
        when(carServiceClient.getCarById(anyString())).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> rentalService.createRental(rentalRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Car not found");

        verify(carServiceClient, times(1)).getCarById("car123");
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    @DisplayName("Bitiş tarihi başlangıç tarihinden önce ise hata vermeli")
    void whenEndDateBeforeStartDate_thenThrowException() {
        // Given
        rentalRequest.setEndDate(LocalDate.now().minusDays(1));
        when(carServiceClient.getCarById(anyString())).thenReturn(carDto);

        // When & Then
        assertThatThrownBy(() -> rentalService.createRental(rentalRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("End date must be after start date");

        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    @DisplayName("Ödeme başarısız olunca rental status CANCELLED olmalı")
    void whenPaymentFails_thenRentalStatusCancelled() {
        // Given
        when(carServiceClient.getCarById("car123")).thenReturn(carDto);
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);
        when(paymentServiceClient.processPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Payment failed"));

        // When & Then
        assertThatThrownBy(() -> rentalService.createRental(rentalRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Payment processing failed");

        verify(rentalRepository, atLeast(2)).save(any(Rental.class));
    }

    @Test
    @DisplayName("Toplam fiyat doğru hesaplanmalı")
    void whenCreateRental_thenTotalPriceCalculatedCorrectly() {
        // Given
        when(carServiceClient.getCarById("car123")).thenReturn(carDto);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            Rental rental = invocation.getArgument(0);
            // 5 days * 750 = 3750
            assertThat(rental.getTotalPrice()).isEqualByComparingTo(new BigDecimal("3750.00"));
            return rental;
        });
        when(paymentServiceClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // When
        rentalService.createRental(rentalRequest);

        // Then
        verify(rentalRepository, atLeast(1)).save(any(Rental.class));
    }

    // ==================== GET RENTAL TESTS ====================

    @Test
    @DisplayName("ID ile kiralama bulunmalı")
    void whenGetRentalById_thenReturnRental() {
        // Given
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));

        // When
        RentalResponse response = rentalService.getRentalById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCarId()).isEqualTo("car123");
        verify(rentalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Olmayan ID ile arama yapılınca hata vermeli")
    void whenGetRentalByNonExistingId_thenThrowException() {
        // Given
        when(rentalRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> rentalService.getRentalById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(rentalRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("User ID'ye göre kiralamalar listelenmeli")
    void whenGetRentalsByUserId_thenReturnRentals() {
        // Given
        List<Rental> rentals = Arrays.asList(testRental);
        when(rentalRepository.findByUserId(1L)).thenReturn(rentals);

        // When
        List<RentalResponse> responses = rentalService.getRentalsByUserId(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(1L);
        verify(rentalRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Car ID'ye göre kiralamalar listelenmeli")
    void whenGetRentalsByCarId_thenReturnRentals() {
        // Given
        List<Rental> rentals = Arrays.asList(testRental);
        when(rentalRepository.findByCarId("car123")).thenReturn(rentals);

        // When
        List<RentalResponse> responses = rentalService.getRentalsByCarId("car123");

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCarId()).isEqualTo("car123");
        verify(rentalRepository, times(1)).findByCarId("car123");
    }

    // ==================== UPDATE STATUS TESTS ====================

    @Test
    @DisplayName("Kiralama durumu güncellenebilmeli")
    void whenUpdateRentalStatus_thenStatusUpdated() {
        // Given
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);

        // When
        RentalResponse response = rentalService.updateRentalStatus(1L, RentalStatus.ACTIVE);

        // Then
        assertThat(response).isNotNull();
        verify(rentalRepository, times(1)).findById(1L);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    // ==================== CANCEL RENTAL TESTS ====================

    @Test
    @DisplayName("PENDING durumundaki kiralama iptal edilebilmeli")
    void whenCancelPendingRental_thenSuccess() {
        // Given
        testRental.setStatus(RentalStatus.PENDING);
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);

        // When
        rentalService.cancelRental(1L);

        // Then
        verify(rentalRepository, times(1)).findById(1L);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    @DisplayName("ACTIVE durumundaki kiralama iptal edilemez")
    void whenCancelActiveRental_thenThrowException() {
        // Given
        testRental.setStatus(RentalStatus.ACTIVE);
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));

        // When & Then
        assertThatThrownBy(() -> rentalService.cancelRental(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot cancel");

        verify(rentalRepository, times(1)).findById(1L);
        verify(rentalRepository, never()).save(any(Rental.class));
    }
}

