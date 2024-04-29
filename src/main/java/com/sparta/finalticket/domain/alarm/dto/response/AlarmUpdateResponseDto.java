package com.sparta.finalticket.domain.alarm.dto.response;

import com.sparta.finalticket.domain.alarm.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmUpdateResponseDto {

    private Long id;
    private String content;
    private Boolean state;
    private Long userId;
    private Long gameId;
    private Boolean isRead;
    private Priority priority;
}
