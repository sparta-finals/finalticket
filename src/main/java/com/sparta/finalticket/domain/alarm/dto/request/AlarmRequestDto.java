package com.sparta.finalticket.domain.alarm.dto.request;

import com.sparta.finalticket.domain.alarm.entity.AlarmType;
import com.sparta.finalticket.domain.alarm.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRequestDto {

    private String content;

    private Boolean state;

    private String message;

    private int timeout;

    private Boolean isRead;

    private Priority priority;

    private String groupName;

    private LocalDateTime scheduledTime;

    private String teamName;

    private AlarmType alarmType;

    private LocalDateTime alarmTime; // 알람 시간
}
