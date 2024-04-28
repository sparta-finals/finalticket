package com.sparta.finalticket.domain.alarm.controller;

import com.sparta.finalticket.domain.alarm.dto.AlarmRequestDto;
import com.sparta.finalticket.domain.alarm.dto.AlarmResponseDto;
import com.sparta.finalticket.domain.alarm.service.AlarmService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/games/{gameId}/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<AlarmResponseDto> createAlarm(@PathVariable(name = "gameId") Long gameId,
                                                        @RequestBody AlarmRequestDto alarmRequestDto,
                                                        HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        AlarmResponseDto createdAlarm = alarmService.createAlarm(user, gameId, alarmRequestDto);
        return ResponseEntity.ok(createdAlarm);
    }

    @GetMapping("/{alarmId}")
    public ResponseEntity<AlarmResponseDto> getAlarmById(@PathVariable(name = "gameId") Long gameId,
                                                         @PathVariable(name = "alarmId") Long alarmId,
                                                         HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        AlarmResponseDto alarmResponseDto = alarmService.getAlarmById(gameId, alarmId, user);
        return ResponseEntity.ok().body(alarmResponseDto);
    }

    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable(name = "gameId") Long gameId,
                                            @PathVariable(name = "alarmId") Long alarmId) {
        alarmService.deleteAlarm(gameId, alarmId);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/alarms/{userId}")
    public void handleAlarmEvent(@DestinationVariable Long userId, String message) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/alarms", message);
    }
}
