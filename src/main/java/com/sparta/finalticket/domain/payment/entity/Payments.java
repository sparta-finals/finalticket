package com.sparta.finalticket.domain.payment.entity;

import com.sparta.finalticket.domain.ticket.entity.Ticket;
import com.sparta.finalticket.domain.timeStamped.TimeStamped;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Payments extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long price;
    private PaymentStatus status;
    private String paymentUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "payments")
    private Ticket ticket;


    @Builder
    public Payments(Long price, User user, PaymentStatus status, Ticket ticket) {
        this.price = price;
        this.user = user;
        this.status = status;
        this.ticket = ticket;
    }

    public void changePaymentBySuccess(PaymentStatus status, String paymentUid) {
        this.status = status;
        this.paymentUid = paymentUid;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setPaymentUid(String paymentUid) {
        this.paymentUid = paymentUid;
    }

}