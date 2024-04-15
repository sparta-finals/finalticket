package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.alarm.repository.AlarmRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import com.sparta.finalticket.global.exception.alarm.AlarmNotFoundException;
import com.sparta.finalticket.global.exception.alarm.AlarmUserNotFoundException;
import com.sparta.finalticket.global.exception.alarm.SseEmitterSendEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribeAlarm(User user) {
        Long userId = user.getId();
        String alarmContent = "알람: " + user.getNickname() + "님, 티켓이 발매되었습니다!";

        SseEmitter emitter = createAlarmUser(userId, alarmContent);

        emitter.onCompletion(() -> sseEmitters.remove(userId));
        emitter.onTimeout(() -> sseEmitters.remove(userId));
        emitter.onError((e) -> sseEmitters.remove(userId));

        return emitter;
    }

    public SseEmitter createAlarmUser(Long userId, String alarmContent) {
        User user = getUserAlarmById(userId);

        Alarm alarm = new Alarm();
        alarm.setContent(alarmContent);
        alarm.setState(alarm.getState() != null ? alarm.getState() : false);
        alarm.setUser(user);

        alarmRepository.save(alarm);

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
}

