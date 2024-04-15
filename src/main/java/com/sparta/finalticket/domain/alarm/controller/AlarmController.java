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
@RequestMapping("/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @GetMapping
    public ResponseEntity<SseEmitter> getAlarm(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        SseEmitter sseEmitter = alarmService.subscribeAlarm(user);
        AlarmService.sseEmitters.put(user.getId(), sseEmitter);
        return ResponseEntity.ok().body(sseEmitter);
    }

    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable(name = "alarmId") Long alarmId) {
        alarmService.deleteAlarm(alarmId);
        return ResponseEntity.noContent().build();
    }
}
