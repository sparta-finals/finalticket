package com.sparta.finalticket.domain.alarm.dto.response;

import com.sparta.finalticket.domain.alarm.entity.AlarmLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlarmLogResponseDto {

    private Long id;
    private Long alarmId;
    private LocalDateTime receivedAt;

    public AlarmLogResponseDto(AlarmLog alarmLog) {
        this.id = alarmLog.getId();
        this.alarmId = alarmLog.getAlarm().getId();
        this.receivedAt = alarmLog.getReceivedAt();
    }
}
