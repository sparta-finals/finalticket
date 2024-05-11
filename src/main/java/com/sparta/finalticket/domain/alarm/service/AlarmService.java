package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.dto.request.AlarmRequestDto;
import com.sparta.finalticket.domain.alarm.dto.request.AlarmUpdateRequestDto;
import com.sparta.finalticket.domain.alarm.dto.request.CustomAlarmRequestDto;
import com.sparta.finalticket.domain.alarm.dto.response.AlarmListResponseDto;
import com.sparta.finalticket.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.finalticket.domain.alarm.dto.response.AlarmUpdateResponseDto;
import com.sparta.finalticket.domain.alarm.dto.response.CustomAlarmResponseDto;
import com.sparta.finalticket.domain.alarm.entity.*;
import com.sparta.finalticket.domain.alarm.repository.AlarmGroupRepository;
import com.sparta.finalticket.domain.alarm.repository.AlarmLogRepository;
import com.sparta.finalticket.domain.alarm.repository.AlarmRepository;
import com.sparta.finalticket.domain.alarm.repository.CustomAlarmRepository;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import com.sparta.finalticket.global.exception.alarm.AlarmGameNotFoundException;
import com.sparta.finalticket.global.exception.alarm.AlarmNotFoundException;
import com.sparta.finalticket.global.exception.alarm.AlarmUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final AlarmLogRepository alarmLogRepository;
    private final AlarmGroupRepository alarmGroupRepository;
    private final CustomAlarmRepository customAlarmRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final RedisAlarmCacheService redisCacheService;
    private final DistributedAlarmService distributedAlarmService;
    private final AlarmDeliveryService alarmDeliveryService;
    private final AlarmRetryService alarmRetryService;

    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public AlarmResponseDto createAlarm(User user, Long gameId, AlarmRequestDto alarmRequestDto) {
        // 중요도 정보를 가져와서 설정
        Priority priority = alarmRequestDto.getPriority();

        // 알림 내용을 구성
        String alarmContent = "알람: " + user.getNickname() + "경기가 곧 시작됩니다. 좌석을 확인하세요! " + alarmRequestDto.getMessage();

        // 쿼리 최적화: 게임 조회를 게임 ID로 바로 수행
        Game game = getGameAlarmById(gameId);

        // 캐시에 저장할 때 사용할 timeout 값 설정
        int timeout = alarmRequestDto.getTimeout();

        // 알림 그룹 생성 및 알림에 할당
        AlarmGroup group = new AlarmGroup();
        group.setGroupName("관련 알림 그룹"); // 그룹 이름은 필요에 따라 설정

        // 분산 락 획득
        RLock lock = distributedAlarmService.getLock(user.getId());
        // 락 획득 시도
        boolean isLocked = distributedAlarmService.tryLock(lock, 10, 60);
        if (isLocked) {
            try {
                // createAlarm 메서드에서 우선순위 값 설정
                Alarm alarm = new Alarm(alarmContent, true, true, user, game, alarmRequestDto.getPriority(), group);

                // 새로운 알람 유형을 설정
                alarm.setAlarmType(alarmRequestDto.getAlarmType());

                // 새로운 세부 정보 설정
                alarm.setScheduledTime(alarmRequestDto.getScheduledTime());
                alarm.setTeamName(alarmRequestDto.getTeamName());

                // 알람 시간 가져오기
                alarm.setAlarmTime(alarmRequestDto.getAlarmTime());

                // 알람 그룹 생성 또는 가져오기
                AlarmGroup groups = getOrCreateAlarmGroup(alarmRequestDto.getGroupName());

                alarm.setGroup(groups);

                alarmRepository.save(alarm);

                // 알림을 생성하는 시점의 시간을 receivedAt 변수에 할당
                LocalDateTime receivedAt = LocalDateTime.now();

                // 알림 로그 생성
                AlarmLog alarmLog = new AlarmLog(alarm, receivedAt);
                alarmLogRepository.save(alarmLog);

                // 캐시에 알림 데이터 저장
                String cacheKey = "alarm:user:" + user.getId() + ":game:" + game.getId();
                redisCacheService.setAlarm(cacheKey, alarmContent, timeout);

                // WebSocket을 통해 알림 전송
                messagingTemplate.convertAndSendToUser(user.getId().toString(), "/topic/alarms", alarmContent);

                // 생성된 알람에 대한 응답을 생성합니다. 필요한 필드 값들을 설정하여 AlarmResponseDto 객체를 반환합니다.
                AlarmResponseDto responseDto = new AlarmResponseDto(alarm.getId(), alarmContent, alarm.getState(), user.getId(), game.getId(), alarm.getIsRead(), priority, groups);

                return responseDto;
            } finally {
                // 분산 락 해제
                distributedAlarmService.unlock(lock);
            }
        } else {
            throw new RuntimeException("락을 획득하지 못했습니다.");
        }
    }

    @Transactional
    public AlarmResponseDto getAlarmById(Long gameId, Long alarmId, User user) {
        Long userId = user.getId();
        User users = getUserAlarmById(userId);
        String alarmContent = "알람: " + users.getNickname() + "경기가 곧 시작됩니다. 좌석을 확인하세요!";
        String cacheKey = "alarm:user:" + userId + ":game:" + gameId;

        // Redis에서 알림 데이터 조회
        String cachedAlarmContent = redisCacheService.getAlarm(cacheKey);
        if (cachedAlarmContent != null) {
            // 캐시된 데이터가 있다면 WebSocket을 통해 알림 전송
            messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/alarms", cachedAlarmContent);
            // getAlarmById 메서드에서 AlarmResponseDto 객체 생성 시 매개변수의 오타 수정
            return new AlarmResponseDto(alarmId, alarmContent, true, userId, gameId, true, Priority.HIGH, new AlarmGroup());

        }

        // 쿼리 최적화: 게임 조회를 게임 ID로 바로 수행
        Game game = getGameAlarmById(gameId);

        // 캐시에 저장할 때 사용할 timeout 값 설정
        int timeout = 60000; // 예시 값 (60초)

        RLock lock = distributedAlarmService.getLock(userId);
        boolean isLocked = distributedAlarmService.tryLock(lock, 10, 60);
        if (isLocked) {
            try {
                // WebSocket을 통해 알림 전송
                messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/alarms", alarmContent);

                Alarm alarm = alarmRepository.findById(alarmId)
                        .orElseThrow(() -> new AlarmNotFoundException("알림을 찾을 수 없습니다."));
                Priority priority = alarm.getPriority();
                AlarmGroup group = alarm.getGroup();

                // 생성한 AlarmResponseDto를 반환합니다.
                // getAlarmById 메서드에서 AlarmResponseDto 객체 생성 시 매개변수의 오타 수정
                return new AlarmResponseDto(alarmId, alarmContent, true, userId, gameId, true, priority, group);
            } finally {
                // 분산 락 해제
                distributedAlarmService.unlock(lock);
            }
        } else {
            throw new AlarmNotFoundException("락을 획득하지 못했습니다");
        }
    }

    @Transactional
    public AlarmUpdateResponseDto updateAlarm(User user, Long gameId, Long alarmId, AlarmUpdateRequestDto alarmUpdateRequestDto) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();

            // 새로운 알람 유형을 설정
            alarm.setAlarmType(alarmUpdateRequestDto.getAlarmType());

            // 알람 업데이트
            alarm.setContent(alarmUpdateRequestDto.getContent());
            alarm.setPriority(alarmUpdateRequestDto.getPriority());

            alarmRepository.save(alarm);

            // Update the cache
            String cacheKey = "alarm:user:" + user.getId() + ":game:" + gameId;
            String updatedAlarmContent = "알람: " + user.getNickname() + "님, " + alarmUpdateRequestDto.getContent();
            redisCacheService.updateCache(cacheKey, updatedAlarmContent, alarmUpdateRequestDto.getTimeout());

            // 알람 업데이트 응답 생성
            return new AlarmUpdateResponseDto(alarm.getId(), alarm.getContent(), alarm.getState(), user.getId(), gameId, alarm.getIsRead(), alarm.getPriority());
        } else {
            throw new AlarmNotFoundException("알람을 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void deleteAlarm(Long gameId, Long alarmId) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();
            User user = alarm.getUser();
            Long userId = user.getId();
            Game game = alarm.getGame();

            // 알림 삭제
            alarmRepository.delete(alarm);

            // 캐시에서 알림 데이터 삭제
            String cacheKey = "alarm:user:" + userId + ":game:" + gameId;
            redisCacheService.deleteAlarm(cacheKey);

            // WebSocket을 통해 알림 전송
            messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/alarms", alarm.getContent());
        } else {
            throw new AlarmNotFoundException("알림을 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void markAlarmAsRead(Long gameId, Long alarmId) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();
            alarm.setIsRead(true); // 알림을 읽음으로 표시

            // 해당 게임의 알림만을 대상으로 알림을 읽었음을 표시합니다.
            if (!alarm.getGame().getId().equals(gameId)) {
                throw new AlarmNotFoundException("해당 경기의 알림을 찾을 수 없습니다.");
            }

            alarmRepository.save(alarm);
        } else {
            throw new AlarmNotFoundException("알림을 찾을 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<AlarmListResponseDto> getAllAlarms(User user, Long gameId) {
        // 유저와 게임을 모두 고려하여 알람을 검색하고 생성일에 따라 정렬합니다.
        List<Alarm> alarmList = alarmRepository.findByUserAndGameIdOrderByCreatedAtDesc(user, gameId);
        return alarmList.stream()
                .map(AlarmListResponseDto::new)
                .sorted(Comparator.comparing(AlarmListResponseDto::getPriority).reversed()
                        .thenComparing(Comparator.comparing(AlarmListResponseDto::getCreatedAt).reversed()))
                .toList();
    }

    @Transactional
    public void resendAlarm(User user, Long gameId, Long alarmId) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();

            // 알림 전송 시도
            boolean deliverySuccess = alarmDeliveryService.sendAlarm(user, alarm);

            if (!deliverySuccess) {
                // 전송 실패 시 재시도
                alarmRetryService.retryAlarm(user, alarm);
            }
        } else {
            throw new AlarmNotFoundException("알림을 찾을 수 없습니다.");
        }
    }

    public CustomAlarmResponseDto createCustomAlarm(User user,@RequestBody CustomAlarmRequestDto customAlarmResponseDto) {
        // 사용자가 작성한 알림 내용과 필요한 정보로 CustomAlarm 엔티티 생성 및 저장
        CustomAlarm customAlarm = CustomAlarm.builder()
                .content(customAlarmResponseDto.getContent())
                .user(user)
                .build();
        customAlarmRepository.save(customAlarm);

        // 생성된 알람에 대한 응답을 생성하여 반환
        return new CustomAlarmResponseDto(customAlarm.getId(), customAlarm.getContent());
    }

    public List<CustomAlarmResponseDto> getAllCustomAlarms(User user) {
        // 사용자가 작성한 모든 알림을 조회하여 DTO로 변환하여 반환
        List<CustomAlarm> customAlarmList = customAlarmRepository.findByUser(user);
        return customAlarmList.stream()
                .map(customAlarm -> new CustomAlarmResponseDto(customAlarm.getId(), customAlarm.getContent()))
                .toList();
    }


    private AlarmGroup getOrCreateAlarmGroup(String groupName) {
        Optional<AlarmGroup> optionalGroup = alarmGroupRepository.findByGroupName(groupName);
        return optionalGroup.orElseGet(() -> {
            AlarmGroup newGroup = new AlarmGroup();
            newGroup.setGroupName(groupName);
            return alarmGroupRepository.save(newGroup);
        });
    }

    private User getUserAlarmById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new AlarmUserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Game getGameAlarmById(Long gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new AlarmGameNotFoundException("경기를 찾을 수 없습니다."));
    }
}