package com.sparta.finalticket.domain.alarm.controller;

import com.sparta.finalticket.domain.alarm.service.AlarmService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    public static final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @GetMapping("/v1/alarms")
    public ResponseEntity<SseEmitter> getAlarm(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        SseEmitter sseEmitter = alarmService.getAlarm(user);
        return ResponseEntity.ok().body(sseEmitter);
    }

    @DeleteMapping("/v1/alarms/{id}")
    public ResponseEntity<Void> deleteAlarm(
        @PathVariable Long id,
        HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        alarmService.deleteAlarm(id, user);
        return ResponseEntity.noContent().build();
    }
}
