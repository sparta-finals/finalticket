package com.sparta.finalticket.domain.alarm.controller;

import com.sparta.finalticket.domain.alarm.dto.request.AlarmGroupRequestDto;
import com.sparta.finalticket.domain.alarm.dto.request.AlarmRequestDto;
import com.sparta.finalticket.domain.alarm.dto.request.AlarmUpdateRequestDto;
import com.sparta.finalticket.domain.alarm.dto.request.CustomAlarmRequestDto;
import com.sparta.finalticket.domain.alarm.dto.response.*;
import com.sparta.finalticket.domain.alarm.entity.AlarmGroup;
import com.sparta.finalticket.domain.alarm.entity.AlarmLog;
import com.sparta.finalticket.domain.alarm.service.AlarmGroupService;
import com.sparta.finalticket.domain.alarm.service.AlarmLogService;
import com.sparta.finalticket.domain.alarm.service.AlarmService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/games/{gameId}/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final AlarmLogService alarmLogService;
    private final AlarmGroupService alarmGroupService;
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

    @PutMapping("/{alarmId}")
    public ResponseEntity<AlarmUpdateResponseDto> updateAlarm(@PathVariable(name = "gameId") Long gameId,
                                                              @PathVariable(name = "alarmId") Long alarmId,
                                                              @RequestBody AlarmUpdateRequestDto alarmUpdateRequestDto,
                                                              HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        AlarmUpdateResponseDto updatedAlarm = alarmService.updateAlarm(user, gameId, alarmId, alarmUpdateRequestDto);
        return ResponseEntity.ok(updatedAlarm);
    }

    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable(name = "gameId") Long gameId,
                                            @PathVariable(name = "alarmId") Long alarmId) {
        alarmService.deleteAlarm(gameId, alarmId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{alarmId}/read")
    public ResponseEntity<Void> markAlarmAsRead(@PathVariable(name = "gameId") Long gameId,
                                                @PathVariable(name = "alarmId") Long alarmId) {
        alarmService.markAlarmAsRead(gameId, alarmId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AlarmListResponseDto>> getAllAlarms(HttpServletRequest httpServletRequest,
                                                                   @PathVariable(name = "gameId") Long gameId) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<AlarmListResponseDto> alarmList = alarmService.getAllAlarms(user, gameId);
        return ResponseEntity.ok(alarmList);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AlarmLogResponseDto>> getAlarmHistory(@PathVariable(name = "gameId") Long gameId,
                                                                     HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<AlarmLogResponseDto> alarmLogList = alarmLogService.getAlarmHistory(gameId, user);
        return ResponseEntity.ok(alarmLogList);
    }

    // 새로운 알림 그룹 생성 엔드포인트
    @PostMapping("/groups")
    public ResponseEntity<AlarmGroupResponseDto> createAlarmGroup(@RequestBody AlarmGroupRequestDto requestDto, @PathVariable(name = "gameId") String gameId) {
        AlarmGroup group = alarmGroupService.createAlarmGroup(requestDto.getGroupName());
        AlarmGroupResponseDto responseDto = new AlarmGroupResponseDto(group.getId(), group.getGroupName());
        return ResponseEntity.ok(responseDto);
    }

    // 기존의 알림 그룹 조회 메서드
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<AlarmGroupResponseDto> getAlarmGroupById(@PathVariable(name = "groupId") Long groupId) {
        AlarmGroup group = alarmGroupService.getAlarmGroupById(groupId);
        AlarmGroupResponseDto responseDto = new AlarmGroupResponseDto(group.getId(), group.getGroupName());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{alarmId}/resend")
    public ResponseEntity<Void> resendAlarm(@PathVariable(name = "gameId") Long gameId,
                                            @PathVariable(name = "alarmId") Long alarmId,
                                            HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        alarmService.resendAlarm(user, gameId, alarmId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/custom")
    public ResponseEntity<CustomAlarmResponseDto> createCustomAlarm(@RequestBody CustomAlarmRequestDto customAlarmRequestDto,
                                                                    HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        CustomAlarmResponseDto createdCustomAlarm = alarmService.createCustomAlarm(user, customAlarmRequestDto);
        return ResponseEntity.ok(createdCustomAlarm);
    }

    @GetMapping("/custom")
    public ResponseEntity<List<CustomAlarmResponseDto>> getAllCustomAlarms(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<CustomAlarmResponseDto> customAlarmList = alarmService.getAllCustomAlarms(user);
        return ResponseEntity.ok(customAlarmList);
    }


    /*
     * 이 메서드는 WebSocket을 통해 알림 이벤트를 처리하고, 특정 사용자에게 메시지를 보내는 역할을 합니다.
     *
     * @param userId 알림을 받을 사용자의 ID
     * @param message 전송할 알림 메시지
     */
    @MessageMapping("/alarms/{userId}")
    public void handleAlarmEvent(@DestinationVariable Long userId, String message) {
        // messagingTemplate을 사용하여 특정 사용자에게 메시지를 전송합니다.
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/alarms", message);
    }

    // 브로드캐스트 메서드 추가
    @MessageMapping("/broadcast")
    public void broadcastNotification(String message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    // 게임 점수 업데이트를 클라이언트에게 전송하는 메서드
    public void sendGameScoreUpdate(Long gameId, int score) {
        // 게임 점수 업데이트 메시지 생성
        String message = "게임 ID " + gameId + "의 점수가 " + score + "점으로 업데이트되었습니다.";

        // WebSocket을 통해 메시지 전송
        messagingTemplate.convertAndSend("/update/gameScore", message);
    }

    // 티켓 가용성 업데이트를 클라이언트에게 전송하는 메서드
    public void sendTicketAvailabilityUpdate(Long gameId, boolean available) {
        // 티켓 가용성 업데이트 메시지 생성
        String message = "게임 ID " + gameId + "의 티켓 가용성이 " + (available ? "가능" : "불가능") + "으로 업데이트되었습니다.";

        // WebSocket을 통해 메시지 전송
        messagingTemplate.convertAndSend("/update/ticketAvailability", message);
    }
}
