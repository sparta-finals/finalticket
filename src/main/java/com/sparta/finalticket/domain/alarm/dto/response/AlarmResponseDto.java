package com.sparta.finalticket.domain.alarm.dto.response;

import com.sparta.finalticket.domain.alarm.entity.AlarmGroup;
import com.sparta.finalticket.domain.alarm.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmResponseDto {

    private Long id;
    private String content;
    private Boolean state;
    private Long userId;
    private Long gameId;
    private Boolean isRead;
    private Priority priority;
    private AlarmGroup groups;

    public AlarmResponseDto(Long id, String alarmContent, Boolean state, Long userId, Long gameId, Boolean isRead, Priority priority, AlarmGroup groups) {
        this.id = id;
        this.content = alarmContent;
        this.state = state;
        this.userId = userId;
        this.gameId = gameId;
        this.isRead = isRead;
        this.priority = priority;
        this.groups = groups;
    }
}
