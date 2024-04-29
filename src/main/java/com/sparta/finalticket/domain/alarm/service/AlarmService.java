package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.dto.request.AlarmRequestDto;
import com.sparta.finalticket.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.alarm.repository.AlarmRepository;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final RedisAlarmCacheService redisCacheService;
    private final DistributedAlarmService distributedAlarmService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public AlarmResponseDto createAlarm(User user, Long gameId, AlarmRequestDto alarmRequestDto) {
        // 알람 내용을 구성합니다. 이 예시에서는 간단하게 DTO에서 가져온 내용을 사용합니다.
        String alarmContent = "알람: " + user.getNickname() + "님, " + alarmRequestDto.getMessage();

        // 쿼리 최적화: 게임 조회를 게임 ID로 바로 수행
        Game game = getGameAlarmById(gameId);

        // 캐시에 저장할 때 사용할 timeout 값 설정
        int timeout = alarmRequestDto.getTimeout(); // alarmRequestDto에서 timeout 값을 가져옵니다.

        // Alarm 객체 생성
        Alarm alarm = new Alarm(alarmContent, true, true, user, game); // 생성자 호출하여 객체 생성

        alarmRepository.save(alarm);

        // 캐시에 알림 데이터 저장
        String cacheKey = "alarm:user:" + user.getId() + ":game:" + game.getId();
        redisCacheService.setAlarm(cacheKey, alarmContent, timeout);

        // WebSocket을 통해 알림 전송
        messagingTemplate.convertAndSendToUser(user.getId().toString(), "/queue/alarms", alarmContent);

        // 생성된 알람에 대한 응답을 생성합니다. 필요한 필드 값들을 설정하여 AlarmResponseDto 객체를 반환합니다.
        AlarmResponseDto responseDto = new AlarmResponseDto(alarm.getId(), alarmContent, alarm.getState(), user.getId(), game.getId(), alarm.getIsRead());

        return responseDto;
    }


    @Transactional
    public AlarmResponseDto getAlarmById(Long gameId, Long alarmId, User user) {
        Long userId = user.getId();
        User users = getUserAlarmById(userId);
        String alarmContent = "알람: " + users.getNickname() + "님, 티켓이 발매되었습니다!";
        String cacheKey = "alarm:user:" + userId + ":game:" + gameId;

        // Redis에서 알림 데이터 조회
        String cachedAlarmContent = redisCacheService.getAlarm(cacheKey);
        if (cachedAlarmContent != null) {
            // 캐시된 데이터가 있다면 WebSocket을 통해 알림 전송
            messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/alarms", cachedAlarmContent);
            return new AlarmResponseDto(alarmId, alarmContent, true, userId, gameId, true);
        }

        // 쿼리 최적화: 게임 조회를 게임 ID로 바로 수행
        Game game = getGameAlarmById(gameId);

        // 캐시에 저장할 때 사용할 timeout 값 설정
        int timeout = 60000; // 예시 값 (60초)

        RLock lock = distributedAlarmService.getLock(userId);
        try {
            boolean isLocked = distributedAlarmService.tryLock(lock, 10, 60);
            if (isLocked) {
                try {
                    // WebSocket을 통해 알림 전송
                    messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/alarms", alarmContent);

                    // 생성한 AlarmResponseDto를 반환합니다.
                    return new AlarmResponseDto(alarmId, alarmContent, true, userId, gameId, true);
                } finally {
                    // 분산 락 해제
                    distributedAlarmService.unlock(lock);
                }
            } else {
                throw new AlarmNotFoundException("락을 획득하지 못했습니다");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AlarmNotFoundException("락을 획득하는 동안 중단되었습니다");
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
                throw new AlarmNotFoundException("해당 게임의 알림을 찾을 수 없습니다.");
            }

            alarmRepository.save(alarm);
        } else {
            throw new AlarmNotFoundException("알림을 찾을 수 없습니다.");
        }
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