package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.dto.response.AlarmLogResponseDto;
import com.sparta.finalticket.domain.alarm.entity.AlarmLog;
import com.sparta.finalticket.domain.alarm.repository.AlarmLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmLogService {

    private final AlarmLogRepository alarmLogRepository;

    @Transactional
    public List<AlarmLogResponseDto> getAlarmHistory(Long gameId, Long userId) {
        List<AlarmLog> alarmHistory = alarmLogRepository.findByAlarmGameIdAndAlarmUserIdOrderByReceivedAtDesc(gameId, userId);
        return alarmHistory.stream()
                .sorted(Comparator.comparing(AlarmLog::getReceivedAt).reversed()) // 수신 시간에 따라 내림차순으로 정렬
                .map(AlarmLogResponseDto::new)
                .toList();
    }
}
