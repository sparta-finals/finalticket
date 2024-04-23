package com.sparta.finalticket.domain.seatsetting.entity;

import com.sparta.finalticket.domain.seatsetting.repository.SeatSettingRepository;
import com.sparta.finalticket.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatSettingInitializer implements CommandLineRunner {

    private final SeatSettingRepository seatSettingRepository;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // 좌석 초기화
        initSeatSettings("a", 10, SeatTypeEnum.REGULAR);
        initSeatSettings("b", 10, SeatTypeEnum.REGULAR);
        initSeatSettings("c", 10, SeatTypeEnum.REGULAR);
        initSeatSettings("d", 10, SeatTypeEnum.VIP);
        initSeatSettings("e", 10, SeatTypeEnum.PREMIUM);
        userService.test();
    }

    private void initSeatSettings(String row, int maxSeatNumber, SeatTypeEnum seatType) {
        for (int i = 1; i <= maxSeatNumber; i++) {
            String seatNumber = row + i;
            SeatSetting seatSetting = new SeatSetting(null, seatNumber, seatType);
            seatSettingRepository.save(seatSetting);
        }
    }
}