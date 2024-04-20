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
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final RedissonClient redissonClient;

    @Value("${alarm.lock.timeout}")
    private long lockTimeout;

    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Transactional
    public SseEmitter alarmInquiry(User user, Long gameId) {
        Long userId = user.getId();
        String alarmContent = "알람: " + user.getNickname() + "님, 티켓이 발매되었습니다!";
        Game game = getGameAlarmById(gameId);

        SseEmitter emitter = subscribeAlarmUser(userId, alarmContent, game);

        emitter.onCompletion(() -> sseEmitters.remove(userId));
        emitter.onTimeout(() -> sseEmitters.remove(userId));
        emitter.onError((e) -> sseEmitters.remove(userId));

        return emitter;
    }

    @Transactional
    public SseEmitter subscribeAlarmUser(Long userId, String alarmContent, Game game) {
        User user = getUserAlarmById(userId);

        Alarm alarm = new Alarm();
        alarm.setContent(alarmContent);
        alarm.setState(true);
        alarm.setUser(user);
        alarm.setGame(game);

        String lockKey = "alarmLock:" + userId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(lockTimeout, TimeUnit.MILLISECONDS)) {
                alarmRepository.save(alarm);
            } else {
                throw new AlarmNotFoundException("알람 구독을 위한 락을 획득하는 데 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AlarmNotFoundException("알람 구독을 위한 락을 획득하는 도중에 인터럽트가 발생했습니다.");
        } finally {
            lock.unlock();
        }

        SseEmitter emitter = new SseEmitter();
        sendEvent(emitter, "content", alarmContent);

        return emitter;
    }

    public void deleteAlarm(Long alarmId) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();
            User user = alarm.getUser();

            alarmRepository.delete(alarm);

            Long userId = user.getId();
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

