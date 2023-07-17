package lt.paymentprocessing.service;

import lt.paymentprocessing.dto.PaymentRequestDto;
import lt.paymentprocessing.dto.PaymentResponseDto;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentResponseDto savePayment(PaymentRequestDto paymentRequestDto);

    PaymentResponseDto getAllValidPaymentsBetweenAmount(BigDecimal amountFrom, BigDecimal amountTill);

    PaymentResponseDto getPaymentCancellationFee(Long paymentId);

    PaymentResponseDto cancelPayment(Long paymentId);
}
