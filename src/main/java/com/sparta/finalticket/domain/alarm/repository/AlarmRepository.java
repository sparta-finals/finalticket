package com.sparta.finalticket.domain.alarm.repository;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
