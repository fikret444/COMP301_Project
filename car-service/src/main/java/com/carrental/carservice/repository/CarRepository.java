package com.carrental.carservice.repository;

import com.carrental.carservice.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarRepository extends MongoRepository<Car, String> {
    // Özel sorgular gerekirse buraya yazılır (örn: findByBrand)
    // Şimdilik boş bırakıyoruz, standart işlemler (Kaydet, Sil, Bul) hazır geliyor.
}