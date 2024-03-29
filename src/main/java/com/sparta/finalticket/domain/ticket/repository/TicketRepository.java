package com.sparta.finalticket.domain.ticket.repository;

import com.sparta.finalticket.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
