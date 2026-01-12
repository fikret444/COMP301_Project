package com.carrental.payment_service.repository;

import com.carrental.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUserId(Long userId);
    
    Optional<Payment> findByRentalId(Long rentalId);
    
    List<Payment> findByTransactionId(String transactionId);
}

