package com.sparta.finalticket.domain.alarm.dto;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmResponseDto {

    private Long id;
    private String content;
    private Boolean state;
    private Long userId;
    private Long gameId;
    private Boolean read;

    public AlarmResponseDto(Alarm alarm) {
        this.id = alarm.getId();
        this.content = alarm.getContent();
        this.state = alarm.getState();
        this.userId = alarm.getUser().getId();
        this.gameId = alarm.getGame().getId();
        this.read = alarm.getIsRead();
    }
}