package com.sparta.finalticket.domain.alarm.repository;

import com.sparta.finalticket.domain.alarm.entity.AlarmLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmLogRepository extends JpaRepository<AlarmLog, Long> {

    List<AlarmLog> findByAlarmGameIdAndAlarmUserIdOrderByReceivedAtDesc(Long gameId, Long userId);
}
