package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.controller.AlarmController;
import com.sparta.finalticket.domain.alarm.entity.Alarm;
import com.sparta.finalticket.domain.alarm.repository.AlarmRepository;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final Map<Integer, SseEmitter> alardControllers = new ConcurrentHashMap<>();

    @Transactional
    public SseEmitter getAlarm(User user) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("content"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        AlarmController.sseEmitters.put(user.getId(), sseEmitter);

        sseEmitter.onCompletion(() -> AlarmController.sseEmitters.remove((user.getId())));
        sseEmitter.onTimeout(() -> AlarmController.sseEmitters.remove(user.getId()));
        sseEmitter.onError((e) -> AlarmController.sseEmitters.remove(user.getId()));

        return sseEmitter;
    }

    @Transactional
    public void deleteAlarm(Long id, User user) {
        Alarm alarm = alarmRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        alarmRepository.deleteById(id);

        SseEmitter sseEmitter = AlarmController.sseEmitters.get(user.getId());
        try {
            sseEmitter.send(SseEmitter.event().name("alarm").data(alardControllers.get(id)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
