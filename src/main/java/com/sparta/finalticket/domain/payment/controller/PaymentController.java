package com.sparta.finalticket.domain.payment.controller;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.sparta.finalticket.domain.payment.dto.request.PaymentCallbackRequest;
import com.sparta.finalticket.domain.payment.dto.request.RequestPayDto;
import com.sparta.finalticket.domain.payment.service.PaymentService;
import com.sparta.finalticket.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/games")
public class PaymentController {

    private final PaymentService paymentService;
    private final TicketService ticketService;

    @GetMapping("/{gameId}/seats/{seatId}/payment")
    public String payment(@PathVariable Long gameId, @PathVariable Long seatId, Model model) {

        RequestPayDto requestDto = paymentService.getRequestPayDto(gameId, seatId);

        model.addAttribute("requestDto", requestDto);
        return "payment";
    }

    @ResponseBody
    @PostMapping("/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request) {
        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);

        log.info("결제 응답={}", iamportResponse.getResponse().toString());

        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
    }
}