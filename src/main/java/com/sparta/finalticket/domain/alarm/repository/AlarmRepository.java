package com.sparta.finalticket.domain.alarm.repository;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByUserAndGameIdOrderByCreatedAtDesc(User user, Long gameId);
}
