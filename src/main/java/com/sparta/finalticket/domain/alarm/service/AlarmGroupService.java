package com.sparta.finalticket.domain.alarm.service;

import com.sparta.finalticket.domain.alarm.entity.AlarmGroup;
import com.sparta.finalticket.domain.alarm.repository.AlarmGroupRepository;
import com.sparta.finalticket.global.exception.alarm.AlarmGroupNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmGroupService {

    private final AlarmGroupRepository alarmGroupRepository;

    public AlarmGroup createAlarmGroup(String groupName) {
        AlarmGroup group = new AlarmGroup();
        group.setGroupName(groupName);
        return alarmGroupRepository.save(group);
    }

    public AlarmGroup getAlarmGroupById(Long groupId) {
        return alarmGroupRepository.findById(groupId)
                .orElseThrow(() -> new AlarmGroupNotFoundException("알람 그룹을 찾을 수 없습니다."));
    }
}
