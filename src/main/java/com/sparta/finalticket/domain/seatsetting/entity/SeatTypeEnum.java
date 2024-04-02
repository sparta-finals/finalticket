package com.sparta.finalticket.domain.seatsetting.entity;

public enum SeatTypeEnum {
    REGULAR(10000),
    VIP(15000),
    PREMIUM(20000);

    private final int price;

    SeatTypeEnum(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
