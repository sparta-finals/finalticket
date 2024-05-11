package com.sparta.finalticket.domain.alarm.repository;

import com.sparta.finalticket.domain.alarm.entity.CustomAlarm;
import com.sparta.finalticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomAlarmRepository extends JpaRepository<CustomAlarm, Long> {

    List<CustomAlarm> findByUser(User user);
}
