package com.carrental.payment_service.service;

import com.carrental.payment_service.dto.PaymentRequest;
import com.carrental.payment_service.dto.PaymentResponse;
import com.carrental.payment_service.model.Payment;
import com.carrental.payment_service.model.PaymentStatus;
import com.carrental.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // Simulate payment processing
        Payment payment = new Payment();
        payment.setRentalId(request.getRentalId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setDescription(request.getDescription());
        payment.setPaymentDate(LocalDateTime.now());
        
        // Generate transaction ID
        payment.setTransactionId(UUID.randomUUID().toString());
        
        // Simulate payment validation (90% success rate for demo)
        if (Math.random() < 0.9) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return convertToResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentByRentalId(Long rentalId) {
        Payment payment = paymentRepository.findByRentalId(rentalId)
                .orElseThrow(() -> new RuntimeException("Payment not found for rental id: " + rentalId));
        return convertToResponse(payment);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        payment.setStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getRentalId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getPaymentDate(),
                payment.getDescription()
        );
    }
}

