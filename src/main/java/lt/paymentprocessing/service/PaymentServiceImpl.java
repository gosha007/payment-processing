package lt.paymentprocessing.service;

import lt.paymentprocessing.dto.PaymentResponseDto;
import lt.paymentprocessing.exception.PaymentCancellationException;
import lt.paymentprocessing.exception.PaymentNotFoundException;
import lt.paymentprocessing.dto.PaymentRequestDto;
import lt.paymentprocessing.model.Payment;
import lt.paymentprocessing.model.PaymentStatusEnum;
import lt.paymentprocessing.repository.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public PaymentResponseDto savePayment(PaymentRequestDto paymentRequestDto) {
        Payment payment = modelMapper.map(paymentRequestDto, Payment.class);

        payment.setNewPaymentFields();

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponseDto.builder()
                .id(savedPayment.getId())
                .message("Payment successfully created")
                .build();
    }

    @Override
    public PaymentResponseDto getAllValidPaymentsBetweenAmount(BigDecimal amountFrom, BigDecimal amountTill) {
        List<Long> paymentIds = paymentRepository.findIdsByStatusAndAmountBetween(
                PaymentStatusEnum.ACTIVE, amountFrom, amountTill);

        return PaymentResponseDto.builder()
                .idList(paymentIds)
                .build();
    }

    @Override
    public PaymentResponseDto getPaymentCancellationFee(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        BigDecimal cancellationFee = new BigDecimal("0.00");
        if (payment.getStatus().equals(PaymentStatusEnum.CANCELLED)) {
            cancellationFee = payment.getCancellationFee();

        } else if (isPaymentCreatedToday(payment)) {
            cancellationFee = calculateCancellationFee(payment);
        }

        return PaymentResponseDto.builder()
                .id(paymentId)
                .cancellationFee(cancellationFee)
                .build();
    }

    @Override
    public PaymentResponseDto cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!isPaymentValidToCancel(payment)) {
            throw new PaymentCancellationException(paymentId);
        }

        BigDecimal cancellationFee = calculateCancellationFee(payment);
        payment.setCancelledPaymentFields(cancellationFee);

        paymentRepository.save(payment);

        return PaymentResponseDto.builder()
                .id(paymentId)
                .message("Payment successfully cancelled")
                .build();
    }

    private boolean isPaymentValidToCancel(Payment payment) {
        return payment.getStatus().equals(PaymentStatusEnum.ACTIVE)
                && isPaymentCreatedToday(payment);
    }

    private boolean isPaymentCreatedToday(Payment payment) {
        LocalDateTime creationDate = payment.getCreateDateTime()
                .truncatedTo(ChronoUnit.DAYS);

        LocalDateTime todayDate = LocalDateTime.now()
                .truncatedTo(ChronoUnit.DAYS);

        return creationDate.isEqual(todayDate);
    }

    /**
     * cancellation fee = hours in system * cancellation coefficient
     */
    private BigDecimal calculateCancellationFee(Payment payment) {
        Duration duration = Duration.between(payment.getCreateDateTime(), LocalDateTime.now());
        long hoursInSystem = duration.getSeconds() / 60 / 60 % 24;
        BigDecimal cancellationFee = payment.getType().getCancellationCoefficient()
                .multiply(new BigDecimal(hoursInSystem));
        return cancellationFee.setScale(2, RoundingMode.HALF_UP);
    }
}
