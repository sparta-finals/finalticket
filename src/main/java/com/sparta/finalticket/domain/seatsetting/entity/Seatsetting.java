package com.sparta.finalticket.domain.seatsetting.entity;

import com.sparta.finalticket.domain.seat.entity.Seat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Seatsetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String seatNumber;

    @OneToOne
    private Seat seat;
}
