package com.carrental.carservice.controller;

import com.carrental.carservice.model.Car;
import com.carrental.carservice.repository.CarRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository carRepository;

    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    // Tüm araçları getir
    @GetMapping
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // Yeni araç ekle
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Car createCar(@RequestBody Car car) {
        return carRepository.save(car);
    }

    // --- YENİ EKLENEN: ARAÇ SİLME ---
    // Kullanımı: DELETE /api/cars/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // İşlem başarılı ama geriye veri dönmüyorum demek (204 kodu)
    public void deleteCar(@PathVariable String id) {
        carRepository.deleteById(id);
    }
}