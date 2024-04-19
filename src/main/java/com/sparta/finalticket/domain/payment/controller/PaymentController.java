package com.sparta.finalticket.domain.payment.controller;

import com.sparta.finalticket.domain.payment.dto.request.RequestPayDto;
import com.sparta.finalticket.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/games")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{gameId}/seats/{seatId}/payment")
    public String payment(@PathVariable Long gameId, @PathVariable Long seatId, Model model) {

        RequestPayDto requestDto = paymentService.getRequestPayDto(gameId, seatId);

        model.addAttribute("requestDto", requestDto);
        return "payment";
    }

    @GetMapping("/success-payment")
    public String successPaymentPage() {
        return "success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {
        return "fail-payment";
    }

}