package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.alarm.repository.AlarmRepository;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import com.sparta.finalticket.global.exception.alarm.AlarmGameNotFoundException;
import com.sparta.finalticket.global.exception.alarm.AlarmNotFoundException;
import com.sparta.finalticket.global.exception.alarm.AlarmUserNotFoundException;
import com.sparta.finalticket.global.exception.alarm.SseEmitterSendEventException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final RedisAlarmCacheService redisCacheService;
    private final DistributedAlarmService distributedAlarmService;

    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Transactional
    public SseEmitter subscribeAlarm(User user, Long gameId) {
        Long userId = user.getId();
        String alarmContent = "알람: " + user.getNickname() + "님, 티켓이 발매되었습니다!";
        String cacheKey = "alarm:user:" + userId + ":game:" + gameId;

        // Redis에서 알림 데이터 조회
        String cachedAlarmContent = redisCacheService.getAlarm(cacheKey);
        if (cachedAlarmContent != null) {
            // 캐시된 데이터가 있다면 바로 사용
            SseEmitter emitter = new SseEmitter();
            sendEvent(emitter, "content", cachedAlarmContent);
            return emitter;
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
                    SseEmitter emitter = createAlarmUser(userId, alarmContent, game, timeout);
                    emitter.onCompletion(() -> {
                        sseEmitters.remove(userId);
                        distributedAlarmService.unlock(lock);
                    });
                    emitter.onTimeout(() -> {
                        sseEmitters.remove(userId);
                        distributedAlarmService.unlock(lock);
                    });
                    emitter.onError((e) -> {
                        sseEmitters.remove(userId);
                        distributedAlarmService.unlock(lock);
                    });
                    return emitter;
                } catch (Exception e) {
                    distributedAlarmService.unlock(lock);
                    throw e;
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
    public SseEmitter createAlarmUser(Long userId, String alarmContent, Game game, int timeout) {
        User user = getUserAlarmById(userId);
        String cacheKey = "alarm:user:" + userId + ":game:" + game.getId();

        // 분산 락 획득
        RLock lock = distributedAlarmService.getLock(userId);
        try {
            boolean isLocked = distributedAlarmService.tryLock(lock, 10, 60);
            if (isLocked) {
                try {
                    // 알람 생성
                    Alarm alarm = new Alarm();
                    alarm.setContent(alarmContent);
                    alarm.setState(true);
                    alarm.setUser(user);
                    alarm.setGame(game);

                    alarmRepository.save(alarm);

                    // SSE emitter 생성 및 이벤트 전송
                    SseEmitter emitter = new SseEmitter();
                    sendEvent(emitter, "content", alarmContent);

                    // 캐시에 알림 데이터 저장
                    redisCacheService.setAlarm(cacheKey, alarmContent, timeout);

                    return emitter;
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
    public void deleteAlarm(Long alarmId) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();
            User user = alarm.getUser();
            Long userId = user.getId();
            Game game = alarm.getGame();
            Long gameId = game.getId();

            // 알림 삭제
            alarmRepository.delete(alarm);

            // 캐시에서 알림 데이터 삭제
            String cacheKey = "alarm:user:" + userId + ":game:" + gameId;
            redisCacheService.deleteAlarm(cacheKey);

            // SSE emitter 제거
            SseEmitter emitter = sseEmitters.remove(userId);
            if (emitter != null) {
                sendEvent(emitter, "alarm", alarm.getContent());
            }
        } else {
            throw new AlarmNotFoundException("알림을 찾을 수 없습니다.");
        }
    }

    private void sendEvent(SseEmitter emitter, String eventName, String eventData) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(eventData));
        } catch (IOException e) {
            throw new SseEmitterSendEventException("SseEmitter에 이벤트를 보내는 중 오류가 발생했습니다.");
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
