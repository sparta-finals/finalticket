package com.sparta.finalticket.domain.seatsetting.entity;

import  com.sparta.finalticket.domain.seatsetting.entity.SeatTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat_setting")
public class SeatSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String seatNumber;

    @Enumerated
    private SeatTypeEnum seatType;

    public int getPrice() {
        return seatType.getPrice();
    }
}