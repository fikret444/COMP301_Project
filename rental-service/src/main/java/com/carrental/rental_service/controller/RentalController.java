package com.carrental.rental_service.controller;

import com.carrental.rental_service.dto.RentalRequest;
import com.carrental.rental_service.dto.RentalResponse;
import com.carrental.rental_service.model.RentalStatus;
import com.carrental.rental_service.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<RentalResponse> createRental(@RequestBody RentalRequest request) {
        try {
            RentalResponse response = rentalService.createRental(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getRentalById(@PathVariable Long id) {
        try {
            RentalResponse response = rentalService.getRentalById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RentalResponse>> getRentalsByUserId(@PathVariable Long userId) {
        List<RentalResponse> rentals = rentalService.getRentalsByUserId(userId);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<List<RentalResponse>> getRentalsByCarId(@PathVariable String carId) {
        List<RentalResponse> rentals = rentalService.getRentalsByCarId(carId);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping
    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        List<RentalResponse> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RentalResponse> updateRentalStatus(
            @PathVariable Long id, 
            @RequestParam RentalStatus status) {
        try {
            RentalResponse response = rentalService.updateRentalStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRental(@PathVariable Long id) {
        try {
            rentalService.cancelRental(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

