package lt.paymentprocessing.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lt.paymentprocessing.dto.PaymentRequestDto;
import lt.paymentprocessing.dto.PaymentResponseDto;
import lt.paymentprocessing.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@Slf4j
public class PaymentController {

    PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<Object> createPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.savePayment(paymentRequestDto);

        return new ResponseEntity<>(paymentResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/payments")
    public ResponseEntity<Object> getAllValidPayments(
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTill) {

        PaymentResponseDto paymentResponseDto = paymentService
                .getAllValidPaymentsBetweenAmount(amountFrom, amountTill);

        return new ResponseEntity<>(paymentResponseDto, HttpStatus.OK);
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<Object> getPayment(@PathVariable Long paymentId) {
        PaymentResponseDto paymentResponseDto = paymentService.getPaymentCancellationFee(paymentId);

        return new ResponseEntity<>(paymentResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<Object> cancelPayment(@PathVariable Long paymentId) {
        PaymentResponseDto paymentResponseDto = paymentService.cancelPayment(paymentId);

        return new ResponseEntity<>(paymentResponseDto, HttpStatus.OK);
    }
}