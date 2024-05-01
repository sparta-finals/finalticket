package com.sparta.finalticket.domain.alarm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmGroupResponseDto {

    private Long id;
    private String groupName;
    private List<AlarmResponseDto> alarms;

    // 생성자 추가
    public AlarmGroupResponseDto(Long id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }
}

