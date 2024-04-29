package com.sparta.finalticket.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRequestDto {

    private String content;

    private Boolean state;

    private String message;

    private int timeout;

    private Boolean isRead;
}