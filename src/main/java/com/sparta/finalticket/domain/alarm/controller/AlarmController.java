package com.sparta.finalticket.domain.alarm.controller;

import com.sparta.finalticket.domain.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    public final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/v1/alarms")
    public SseEmitter getAlarm() throws IOException {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        alarmService.getAlarm(sseEmitter);
        return sseEmitter;
    }

    @DeleteMapping("/v1/alarms/{id}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long id) {
        alarmService.deleteAlarm(id);
        return ResponseEntity.noContent().build();
    }
}
