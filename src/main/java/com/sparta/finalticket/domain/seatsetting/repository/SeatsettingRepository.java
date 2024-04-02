package com.sparta.finalticket.domain.seatsetting.repository;

import com.sparta.finalticket.domain.seatsetting.entity.SeatSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatsettingRepository extends JpaRepository<SeatSetting, Long> {

}
