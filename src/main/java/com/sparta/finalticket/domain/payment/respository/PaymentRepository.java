package com.sparta.finalticket.domain.payment.respository;

import com.sparta.finalticket.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
