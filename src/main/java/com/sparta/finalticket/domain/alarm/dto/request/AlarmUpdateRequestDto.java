package com.sparta.finalticket.domain.alarm.dto.request;

import com.sparta.finalticket.domain.alarm.entity.AlarmType;
import com.sparta.finalticket.domain.alarm.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmUpdateRequestDto {

    private String content;

    private Boolean state;

    private String message;

    private int timeout;

    private Boolean isRead;

    private Priority priority;

    private AlarmType alarmType;
}
