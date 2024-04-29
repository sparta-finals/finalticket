package com.sparta.finalticket.domain.alarm.dto.response;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.alarm.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmListResponseDto {

    private Long id;
    private String content;
    private Boolean state;
    private Long userId;
    private Long gameId;
    private Boolean isRead;
    private Priority priority;
    private LocalDateTime createdAt; // 생성일 추가

    public AlarmListResponseDto(Alarm alarm) {
        this.id = alarm.getId();
        this.content = alarm.getContent();
        this.state = alarm.getState();
        this.userId = alarm.getUser().getId();
        this.gameId = alarm.getGame().getId();
        this.isRead = alarm.getIsRead();
        this.priority = alarm.getPriority();
        this.createdAt = alarm.getCreatedAt(); // 생성일 설정
    }
}
