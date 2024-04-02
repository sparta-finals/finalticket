package com.sparta.finalticket.domain.seatsetting.repository;

import com.sparta.finalticket.domain.seatsetting.entity.Seatsetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatSettingRepository extends JpaRepository<Seatsetting, Long> {

}
