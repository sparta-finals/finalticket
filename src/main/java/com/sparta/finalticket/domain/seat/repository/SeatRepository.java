package com.sparta.finalticket.domain.seat.repository;

import com.sparta.finalticket.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {

}
