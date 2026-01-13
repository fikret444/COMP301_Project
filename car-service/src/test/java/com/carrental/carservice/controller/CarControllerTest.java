package com.carrental.carservice.controller;

import com.carrental.carservice.model.Car;
import com.carrental.carservice.repository.CarRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CarController Integration Test
 * @WebMvcTest: Sadece controller katmanını test eder
 */
@WebMvcTest(CarController.class)
@DisplayName("Car Controller Tests")
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarRepository carRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Car testCar;
    private Car testCar2;

    @BeforeEach
    void setUp() {
        testCar = new Car();
        testCar.setId("car1");
        testCar.setBrand("BMW");
        testCar.setModel("320i");
        testCar.setYear(2024);
        testCar.setDailyPrice(new BigDecimal("750.00"));
        testCar.setAvailable(true);

        testCar2 = new Car();
        testCar2.setId("car2");
        testCar2.setBrand("Mercedes");
        testCar2.setModel("C200");
        testCar2.setYear(2023);
        testCar2.setDailyPrice(new BigDecimal("850.00"));
        testCar2.setAvailable(false);
    }

    // ==================== GET ALL CARS TESTS ====================

    @Test
    @DisplayName("GET /api/cars - Tüm araçları getirmeli")
    void whenGetAllCars_thenReturnCarList() throws Exception {
        // Given
        List<Car> cars = Arrays.asList(testCar, testCar2);
        when(carRepository.findAll()).thenReturn(cars);

        // When & Then
        mockMvc.perform(get("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].brand").value("BMW"))
                .andExpect(jsonPath("$[0].model").value("320i"))
                .andExpect(jsonPath("$[1].brand").value("Mercedes"))
                .andExpect(jsonPath("$[1].model").value("C200"));

        verify(carRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/cars - Araç yoksa boş liste dönmeli")
    void whenGetAllCars_andNoCars_thenReturnEmptyList() throws Exception {
        // Given
        when(carRepository.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carRepository, times(1)).findAll();
    }

    // ==================== CREATE CAR TESTS ====================

    @Test
    @DisplayName("POST /api/cars - Yeni araç oluşturulmalı")
    void whenCreateCar_thenReturn201() throws Exception {
        // Given
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // When & Then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCar)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("car1"))
                .andExpect(jsonPath("$.brand").value("BMW"))
                .andExpect(jsonPath("$.model").value("320i"))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.dailyPrice").value(750.00))
                .andExpect(jsonPath("$.available").value(true));

        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    @DisplayName("POST /api/cars - Geçersiz JSON ile 400 dönmeli")
    void whenCreateCarWithInvalidJson_thenReturn400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("POST /api/cars - Content-Type header eksikse 415 dönmeli")
    void whenCreateCarWithoutContentType_thenReturn415() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/cars")
                        .content(objectMapper.writeValueAsString(testCar)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());

        verify(carRepository, never()).save(any(Car.class));
    }

    // ==================== DELETE CAR TESTS ====================

    @Test
    @DisplayName("DELETE /api/cars/{id} - Araç silinmeli ve 204 dönmeli")
    void whenDeleteCar_thenReturn204() throws Exception {
        // Given
        doNothing().when(carRepository).deleteById(anyString());

        // When & Then
        mockMvc.perform(delete("/api/cars/car1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(carRepository, times(1)).deleteById("car1");
    }

    @Test
    @DisplayName("DELETE /api/cars/{id} - Repository exception fırlatırsa 4xx dönmeli")
    void whenDeleteCarThrowsException_thenReturn4xx() throws Exception {
        // Given
        doThrow(new RuntimeException("Database error"))
                .when(carRepository).deleteById(anyString());

        // When & Then
        mockMvc.perform(delete("/api/cars/car1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(carRepository, times(1)).deleteById("car1");
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("Araç oluştur ve listele - Full flow")
    void whenCreateAndGetCars_thenSuccess() throws Exception {
        // Given
        when(carRepository.save(any(Car.class))).thenReturn(testCar);
        when(carRepository.findAll()).thenReturn(Arrays.asList(testCar));

        // When - Create car
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isCreated());

        // Then - Get all cars
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].brand").value("BMW"));

        verify(carRepository, times(1)).save(any(Car.class));
        verify(carRepository, times(1)).findAll();
    }
}

