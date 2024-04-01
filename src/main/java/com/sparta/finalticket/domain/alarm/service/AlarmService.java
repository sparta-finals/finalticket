package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private static final String EVENT_NAME = "alarm";
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Transactional
    public void getAlarm(SseEmitter sseEmitter) throws IOException {
        sseEmitter.send(SseEmitter.event().name("connect"));
        alarmRepository.findAll().forEach(alarm -> {
            try {
                sseEmitter.send(SseEmitter.event().name(EVENT_NAME).data(alarm));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        sseEmitter.onCompletion(() -> {
            if (emitters.containsValue(sseEmitter)) {
                emitters.values().remove(sseEmitter);
            }
        });
        sseEmitter.onTimeout(() -> {
            if (emitters.containsValue(sseEmitter)) {
                emitters.values().remove(sseEmitter);
            }
        });
        sseEmitter.onError(throwable -> {
            if (emitters.containsValue(sseEmitter)) {
                emitters.values().remove(sseEmitter);
            }
        });
        emitters.put(sseEmitter.hashCode(), sseEmitter);
    }

    @Transactional
    public void deleteAlarm(Long id) {
        alarmRepository.deleteById(id);
        broadcastDeleteAlarm(id);
    }

    private void broadcastDeleteAlarm(Long id) {
        List<SseEmitter> deadEmitters = List.of();
        emitters.values().forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name(EVENT_NAME).data("delete:" + id));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        deadEmitters.forEach(emitters::remove);
    }
}
