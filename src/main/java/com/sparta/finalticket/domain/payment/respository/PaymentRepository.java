package com.sparta.finalticket.domain.payment.respository;

import com.sparta.finalticket.domain.payment.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payments, Long> {

}
