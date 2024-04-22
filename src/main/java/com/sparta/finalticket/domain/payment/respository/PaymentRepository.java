package com.sparta.finalticket.domain.payment.respository;

import com.sparta.finalticket.domain.payment.entity.Payments;
import com.sparta.finalticket.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payments, Long> {


    Payments findByTicket(Ticket ticket);
}
