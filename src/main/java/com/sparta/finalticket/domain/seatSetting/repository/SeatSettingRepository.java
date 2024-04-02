package com.sparta.finalticket.domain.seatsetting.repository;

import com.sparta.finalticket.domain.seatSetting.entity.SeatSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatSettingRepository extends JpaRepository<SeatSetting, Long> {

}
