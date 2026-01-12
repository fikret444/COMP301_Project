package com.carrental.rental_service.service;

import com.carrental.rental_service.client.CarServiceClient;
import com.carrental.rental_service.client.PaymentServiceClient;
import com.carrental.rental_service.dto.*;
import com.carrental.rental_service.model.Rental;
import com.carrental.rental_service.model.RentalStatus;
import com.carrental.rental_service.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CarServiceClient carServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public RentalService(RentalRepository rentalRepository, 
                        CarServiceClient carServiceClient,
                        PaymentServiceClient paymentServiceClient) {
        this.rentalRepository = rentalRepository;
        this.carServiceClient = carServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    @Transactional
    public RentalResponse createRental(RentalRequest request) {
        // 1. Get car information from Car Service
        CarDto car = carServiceClient.getCarById(request.getCarId());
        
        if (car == null) {
            throw new RuntimeException("Car not found with id: " + request.getCarId());
        }

        // 2. Calculate total price
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days <= 0) {
            throw new RuntimeException("End date must be after start date");
        }
        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        // 3. Create rental
        Rental rental = new Rental();
        rental.setCarId(request.getCarId());
        rental.setUserId(request.getUserId());
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setTotalPrice(totalPrice);
        rental.setStatus(RentalStatus.PENDING);
        rental.setNotes(request.getNotes());

        Rental savedRental = rentalRepository.save(rental);

        // 4. Process payment
        PaymentRequest paymentRequest = new PaymentRequest(
                savedRental.getId(),
                request.getUserId(),
                totalPrice,
                request.getPaymentMethod(),
                "Rental payment for car " + request.getCarId()
        );

        PaymentResponse payment = null;
        try {
            payment = paymentServiceClient.processPayment(paymentRequest);
            
            // Update rental status based on payment
            if ("COMPLETED".equals(payment.getStatus())) {
                savedRental.setStatus(RentalStatus.CONFIRMED);
            } else {
                savedRental.setStatus(RentalStatus.CANCELLED);
            }
            rentalRepository.save(savedRental);
        } catch (Exception e) {
            savedRental.setStatus(RentalStatus.CANCELLED);
            rentalRepository.save(savedRental);
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }

        return convertToResponse(savedRental, payment);
    }

    public RentalResponse getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
        return convertToResponse(rental, null);
    }

    public List<RentalResponse> getRentalsByUserId(Long userId) {
        return rentalRepository.findByUserId(userId).stream()
                .map(rental -> convertToResponse(rental, null))
                .collect(Collectors.toList());
    }

    public List<RentalResponse> getRentalsByCarId(String carId) {
        return rentalRepository.findByCarId(carId).stream()
                .map(rental -> convertToResponse(rental, null))
                .collect(Collectors.toList());
    }

    public List<RentalResponse> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(rental -> convertToResponse(rental, null))
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalResponse updateRentalStatus(Long id, RentalStatus status) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
        
        rental.setStatus(status);
        Rental updatedRental = rentalRepository.save(rental);
        return convertToResponse(updatedRental, null);
    }

    @Transactional
    public void cancelRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
        
        if (rental.getStatus() == RentalStatus.ACTIVE || rental.getStatus() == RentalStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel an active or completed rental");
        }
        
        rental.setStatus(RentalStatus.CANCELLED);
        rentalRepository.save(rental);
    }

    private RentalResponse convertToResponse(Rental rental, PaymentResponse payment) {
        RentalResponse response = new RentalResponse();
        response.setId(rental.getId());
        response.setCarId(rental.getCarId());
        response.setUserId(rental.getUserId());
        response.setStartDate(rental.getStartDate());
        response.setEndDate(rental.getEndDate());
        response.setTotalPrice(rental.getTotalPrice());
        response.setStatus(rental.getStatus());
        response.setCreatedAt(rental.getCreatedAt());
        response.setNotes(rental.getNotes());
        response.setPayment(payment);
        return response;
    }
}

