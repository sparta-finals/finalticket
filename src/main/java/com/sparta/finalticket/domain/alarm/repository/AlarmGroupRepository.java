package com.sparta.finalticket.domain.alarm.repository;

import com.sparta.finalticket.domain.alarm.entity.AlarmGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmGroupRepository extends JpaRepository<AlarmGroup, Long> {

    Optional<AlarmGroup> findByGroupName(String groupName);
}
