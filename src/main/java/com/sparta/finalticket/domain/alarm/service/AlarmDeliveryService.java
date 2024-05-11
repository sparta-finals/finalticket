package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AlarmDeliveryService {

    private final SimpMessagingTemplate messagingTemplate;

    public boolean sendAlarm(User user, Alarm alarm) {
        // 알림 내용
        String alarmContent = "알람: " + user.getNickname() + "님, " + alarm.getContent();

        // WebSocket을 통해 알림 전송
        messagingTemplate.convertAndSendToUser(user.getId().toString(), "/queue/alarms", alarmContent);

        // 전송 성공 여부 반환
        return true; // 전송 성공 시
    }
}
