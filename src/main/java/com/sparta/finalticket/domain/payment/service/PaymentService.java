package com.sparta.finalticket.domain.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.sparta.finalticket.domain.payment.dto.request.PaymentCallbackRequest;
import com.sparta.finalticket.domain.payment.dto.request.RequestPayDto;
import com.sparta.finalticket.domain.payment.entity.PaymentStatus;
import com.sparta.finalticket.domain.payment.respository.PaymentRepository;
import com.sparta.finalticket.domain.ticket.entity.Ticket;
import com.sparta.finalticket.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;
    public RequestPayDto getRequestPayDto(Long gameId,Long seatId) {

        log.info(seatId.toString());
        // 티켓 엔티티 조회
        Ticket ticket = ticketRepository.findByGameIdAndSeatId(gameId,seatId);
        log.info(ticket.toString());

        RequestPayDto dto = new RequestPayDto();
        dto.setTicketUid(ticket.getTicketUid());
        dto.setGameName(ticket.getSeat().getGame().getName());
        dto.setPrice(ticket.getSeat().getPrice());
        dto.setUserName(ticket.getUser().getUsername());
        dto.setUserEmail(ticket.getUser().getEmail());
        dto.setUserAddress(ticket.getUser().getAddress());

        return dto;
    }


    public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {

        try {
            // 결제 단건 조회(아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
            // 주문내역 조회
            Ticket ticket = ticketRepository.findTicketAndPayments(request.getTicketUid())
                .orElseThrow(() -> new IllegalArgumentException("주문 내역이 없습니다."));
            // 결제 완료가 아니면
            if (!iamportResponse.getResponse().getStatus().equals("paid")) {
                // 주문, 결제 삭제
                ticketRepository.delete(ticket);
                paymentRepository.delete(ticket.getPayments());

                throw new RuntimeException("결제 미완료");
            }

            // DB에 저장된 결제 금액
            Long price = Long.valueOf(ticket.getSeat().getPrice());
            // 실 결제 금액
            int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

            // 결제 금액 검증
            if (iamportPrice != price) {
                // 주문, 결제 삭제
                ticketRepository.delete(ticket);
                paymentRepository.delete(ticket.getPayments());

                // 결제금액 위변조로 의심되는 결제금액을 취소(아임포트)
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(iamportPrice)));

                throw new RuntimeException("결제금액 위변조 의심");
            }

            // 결제 상태 변경
            ticket.getPayments().changePaymentBySuccess(PaymentStatus.OK, iamportResponse.getResponse().getImpUid());

            return iamportResponse;

        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
