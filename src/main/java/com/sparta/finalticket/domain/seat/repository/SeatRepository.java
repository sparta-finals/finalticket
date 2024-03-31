package com.sparta.finalticket.domain.seat.repository;

import com.sparta.finalticket.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findSeatByGameIdAndSeatsettingIdAndUserId(Long gameId, Long SeatsettingId, Long userId);

}
