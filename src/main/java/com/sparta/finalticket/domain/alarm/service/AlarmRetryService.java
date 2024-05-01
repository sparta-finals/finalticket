package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// AlarmRetryService.java

@Service
@RequiredArgsConstructor
public class AlarmRetryService {

    private final AlarmDeliveryService alarmDeliveryService;

    public void retryAlarm(User user, Alarm alarm) {
        // 일정 시간 후 재시도
        // 여기서는 간단히 재전송 로직을 호출하도록 하였습니다.
        boolean retrySuccess = alarmDeliveryService.sendAlarm(user, alarm);

        // 재시도가 성공한 경우
        if (retrySuccess) {
            // 성공 시 해당 알람의 deliveryAttempts를 증가시킴
            alarm.setDeliveryAttempts(alarm.getDeliveryAttempts() + 1);
        } else {
            // 재시도가 실패한 경우, 예외처리 또는 로그 등을 수행할 수 있음
            // 여기서는 간단히 로그 출력만 하도록 함
            System.out.println("알림 재시도가 실패했습니다.");
        }
    }
}
