package com.sparta.finalticket.domain.seatsetting.entity;

import com.sparta.finalticket.domain.seat.entity.Seat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SeatSetting extends Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String seat;
}
