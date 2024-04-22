package com.sparta.finalticket.domain.payment.dto.request;

import lombok.*;

@Getter
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

    public void setTicketUid(String ticketUid) {
        this.ticketUid = ticketUid;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }



}
