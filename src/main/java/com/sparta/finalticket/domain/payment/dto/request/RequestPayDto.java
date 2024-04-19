package com.sparta.finalticket.domain.payment.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestPayDto {
    private String ticketUid;
    private String gameName;
    private int price;
    private String userName;
    private String userEmail;
    private String userAddress;
}
