package com.carrental.carservice.repository;

import com.carrental.carservice.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CarRepository Integration Test
 * @DataMongoTest: Embedded MongoDB kullanarak repository testleri
 */
@DataMongoTest
@ActiveProfiles("test")
@DisplayName("Car Repository Tests")
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    private Car testCar;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
        
        testCar = new Car();
        testCar.setBrand("BMW");
        testCar.setModel("320i");
        testCar.setYear(2024);
        testCar.setDailyPrice(new BigDecimal("750.00"));
        testCar.setAvailable(true);
    }

    @Test
    @DisplayName("Araç başarıyla kaydedilmeli")
    void whenSaveCar_thenCarIsSaved() {
        // When
        Car savedCar = carRepository.save(testCar);

        // Then
        assertThat(savedCar).isNotNull();
        assertThat(savedCar.getId()).isNotNull();
        assertThat(savedCar.getBrand()).isEqualTo("BMW");
        assertThat(savedCar.getModel()).isEqualTo("320i");
        assertThat(savedCar.getYear()).isEqualTo(2024);
        assertThat(savedCar.getDailyPrice()).isEqualByComparingTo(new BigDecimal("750.00"));
        assertThat(savedCar.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("ID ile araç bulunmalı")
    void whenFindById_thenReturnCar() {
        // Given
        Car savedCar = carRepository.save(testCar);

        // When
        Optional<Car> found = carRepository.findById(savedCar.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getBrand()).isEqualTo("BMW");
        assertThat(found.get().getModel()).isEqualTo("320i");
    }

    @Test
    @DisplayName("Tüm araçlar listelenebilmeli")
    void whenFindAll_thenReturnAllCars() {
        // Given
        Car car2 = new Car();
        car2.setBrand("Mercedes");
        car2.setModel("C200");
        car2.setYear(2023);
        car2.setDailyPrice(new BigDecimal("850.00"));
        car2.setAvailable(false);

        carRepository.save(testCar);
        carRepository.save(car2);

        // When
        List<Car> cars = carRepository.findAll();

        // Then
        assertThat(cars).hasSize(2);
        assertThat(cars).extracting(Car::getBrand).containsExactlyInAnyOrder("BMW", "Mercedes");
    }

    @Test
    @DisplayName("Araç başarıyla silinmeli")
    void whenDeleteCar_thenCarIsDeleted() {
        // Given
        Car savedCar = carRepository.save(testCar);

        // When
        carRepository.deleteById(savedCar.getId());

        // Then
        Optional<Car> deleted = carRepository.findById(savedCar.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Araç bilgileri güncellenebilmeli")
    void whenUpdateCar_thenCarIsUpdated() {
        // Given
        Car savedCar = carRepository.save(testCar);

        // When
        savedCar.setDailyPrice(new BigDecimal("800.00"));
        savedCar.setAvailable(false);
        Car updatedCar = carRepository.save(savedCar);

        // Then
        assertThat(updatedCar.getDailyPrice()).isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(updatedCar.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Olmayan ID ile arama yapılınca boş Optional dönmeli")
    void whenFindByNonExistingId_thenReturnEmpty() {
        // When
        Optional<Car> found = carRepository.findById("nonexisting123");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Tüm araçlar silinebilmeli")
    void whenDeleteAll_thenNoCarsLeft() {
        // Given
        carRepository.save(testCar);
        
        Car car2 = new Car();
        car2.setBrand("Audi");
        car2.setModel("A4");
        car2.setYear(2023);
        car2.setDailyPrice(new BigDecimal("700.00"));
        car2.setAvailable(true);
        carRepository.save(car2);

        // When
        carRepository.deleteAll();

        // Then
        assertThat(carRepository.findAll()).isEmpty();
    }
}

