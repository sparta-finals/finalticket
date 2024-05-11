package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmRetryService {

    private final AlarmDeliveryService alarmDeliveryService;

    private static final Logger logger = LoggerFactory.getLogger(AlarmRetryService.class);

    public void retryAlarm(User user, Alarm alarm) {
        boolean retrySuccess = alarmDeliveryService.sendAlarm(user, alarm);

        if (retrySuccess) {
            // 재전송 성공 시 deliveryAttempts를 증가시킴
            alarm.setDeliveryAttempts(alarm.getDeliveryAttempts() + 1);
        } else {
            // 재전송 실패 시 로그를 출력
            logger.error("알림 재전송이 실패했습니다. Alarm ID: {}, User ID: {}", alarm.getId(), user.getId());
        }
    }
}
