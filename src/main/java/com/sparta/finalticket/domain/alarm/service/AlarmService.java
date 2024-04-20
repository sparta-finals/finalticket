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
    private final DistributedAlarmService distributedAlarmService;

    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Transactional
    public SseEmitter subscribeAlarm(User user, Long gameId) {
        Long userId = user.getId();
        String alarmContent = "알람: " + user.getNickname() + "님, 티켓이 발매되었습니다!";
        Game game = getGameAlarmById(gameId);

        RLock lock = distributedAlarmService.getLock(userId);
        try {
            boolean isLocked = distributedAlarmService.tryLock(lock, 10, 60);
            if (isLocked) {
                try {
                    SseEmitter emitter = createAlarmUser(userId, alarmContent, game);
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
    public SseEmitter createAlarmUser(Long userId, String alarmContent, Game game) {
        User user = getUserAlarmById(userId);

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

    public void deleteAlarm(Long alarmId) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();
            User user = alarm.getUser();

            // 분산 락 획득
            RLock lock = distributedAlarmService.getLock(user.getId());
            try {
                boolean isLocked = distributedAlarmService.tryLock(lock, 10, 60);
                if (isLocked) {
                    try {
                        // 알람 삭제
                        alarmRepository.delete(alarm);

                        // SSE emitter 제거
                        Long userId = user.getId();
                        SseEmitter emitter = sseEmitters.remove(userId);
                        if (emitter != null) {
                            sendEvent(emitter, "alarm", alarm.getContent());
                        }
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


