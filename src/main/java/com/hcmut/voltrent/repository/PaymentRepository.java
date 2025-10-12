package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
