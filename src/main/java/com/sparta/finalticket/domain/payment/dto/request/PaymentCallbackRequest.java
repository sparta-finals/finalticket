package com.sparta.finalticket.domain.payment.dto.request;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentCallbackRequest {
    private String paymentUid; // 결제 고유 번호
    private String ticketUid; // 주문 고유 번호
}