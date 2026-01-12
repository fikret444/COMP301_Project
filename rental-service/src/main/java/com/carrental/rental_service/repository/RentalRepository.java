package com.carrental.rental_service.repository;

import com.carrental.rental_service.model.Rental;
import com.carrental.rental_service.model.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    
    List<Rental> findByUserId(Long userId);
    
    List<Rental> findByCarId(String carId);
    
    List<Rental> findByStatus(RentalStatus status);
    
    List<Rental> findByUserIdAndStatus(Long userId, RentalStatus status);
}

