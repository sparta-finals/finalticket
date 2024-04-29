package com.sparta.finalticket.domain.alarm.entity;

import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmLog extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;

    @Column(nullable = false)
    private LocalDateTime receivedAt; // receivedAt 필드 추가

    public AlarmLog(Alarm alarm, LocalDateTime receivedAt) {
        this.alarm = alarm;
        this.receivedAt = receivedAt;
    }
}
