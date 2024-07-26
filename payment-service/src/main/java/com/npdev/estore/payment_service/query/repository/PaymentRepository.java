package com.npdev.estore.payment_service.query.repository;

import com.npdev.estore.payment_service.query.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
