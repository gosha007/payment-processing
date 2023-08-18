package lt.paymentprocessing.service;

import lt.paymentprocessing.exception.PaymentCancellationException;
import lt.paymentprocessing.model.Payment;
import lt.paymentprocessing.model.PaymentStatusEnum;
import lt.paymentprocessing.model.PaymentTypeEnum;
import lt.paymentprocessing.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    PaymentService paymentService;

    @Mock
    PaymentRepository paymentRepositoryMock;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(new ModelMapper(), paymentRepositoryMock);
    }

    @Test
    void givenPaymentCreatedToday_whenCancelPayment_thenStatusCancelled() {
        // given
        Payment paymentCreatedToday = new Payment();
        paymentCreatedToday.setId(1L);
        paymentCreatedToday.setType(PaymentTypeEnum.TYPE1);
        paymentCreatedToday.setNewPaymentFields();
        paymentCreatedToday.setCreateDateTime(LocalDateTime.now().minusSeconds(1));

        when(paymentRepositoryMock.findById(paymentCreatedToday.getId()))
                .thenReturn(Optional.of(paymentCreatedToday));

        // when
        paymentService.cancelPayment(paymentCreatedToday.getId());

        // then
        assertThat(paymentCreatedToday.getStatus()).isEqualTo(PaymentStatusEnum.CANCELLED);
    }

    @Test
    void givenPaymentCreatedToday_whenCancelPaymentRightBeforeMidnight_thenStatusCancelled() {
        // given
        LocalDateTime today = LocalDateTime.now();

        Payment paymentCreatedToday = new Payment();
        paymentCreatedToday.setId(1L);
        paymentCreatedToday.setType(PaymentTypeEnum.TYPE1);
        paymentCreatedToday.setNewPaymentFields();
        paymentCreatedToday.setCreateDateTime(today);

        LocalDateTime rightBeforeMidnightDateTime = LocalDateTime.of(
                today.getYear(),today.getMonth(),today.getDayOfMonth(), 23,59,59);

        when(paymentRepositoryMock.findById(paymentCreatedToday.getId()))
                .thenReturn(Optional.of(paymentCreatedToday));

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(rightBeforeMidnightDateTime);

            // when
            paymentService.cancelPayment(paymentCreatedToday.getId());

            // then
            assertThat(paymentCreatedToday.getStatus()).isEqualTo(PaymentStatusEnum.CANCELLED);
        }
    }

    @Test
    void givenPaymentCreatedYesterday_whenCancelPayment_thenThrowException() {
        // given
        Payment paymentCreatedYesterday = new Payment();
        paymentCreatedYesterday.setId(1L);
        paymentCreatedYesterday.setNewPaymentFields();
        paymentCreatedYesterday.setCreateDateTime(LocalDateTime.now().minusDays(1));

        when(paymentRepositoryMock.findById(paymentCreatedYesterday.getId()))
                .thenReturn(Optional.of(paymentCreatedYesterday));

        // when, then
        assertThrows(PaymentCancellationException.class,
                () -> paymentService.cancelPayment(paymentCreatedYesterday.getId()));
    }

    @Test
    void givenAlreadyCancelledPayment_whenCancelPayment_thenThrowException() {
        // given
        Payment alreadyCancelledPayment = new Payment();
        alreadyCancelledPayment.setCancelledPaymentFields(BigDecimal.ZERO);
        alreadyCancelledPayment.setCreateDateTime(LocalDateTime.now().minusDays(1));

        when(paymentRepositoryMock.findById(alreadyCancelledPayment.getId()))
                .thenReturn(Optional.of(alreadyCancelledPayment));

        // when, then
        assertThrows(PaymentCancellationException.class,
                () -> paymentService.cancelPayment(alreadyCancelledPayment.getId()));
    }

    @Test
    void givenPaymentCreatedNow_whenCancelPayment_thenCancellationFeeZero() {
        // given
        Payment paymentCreatedNow = new Payment();
        paymentCreatedNow.setId(1L);
        paymentCreatedNow.setNewPaymentFields();

        PaymentTypeEnum mockedPaymentTypeEnum = mock(PaymentTypeEnum.class);
        paymentCreatedNow.setType(mockedPaymentTypeEnum);

        when(paymentCreatedNow.getType().getCancellationCoefficient()).thenReturn(new BigDecimal("0.05"));

        when(paymentRepositoryMock.findById(paymentCreatedNow.getId()))
                .thenReturn(Optional.of(paymentCreatedNow));

        // when
        paymentService.cancelPayment(paymentCreatedNow.getId());

        // then
        assertThat(paymentCreatedNow.getCancellationFee()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void givenPaymentCreatedTodayBefore21Hours_whenCancelPayment_thenCancellationFeeNotZero() {
        // given
        Payment paymentCreatedTodayBefore21Hours = new Payment();
        paymentCreatedTodayBefore21Hours.setId(1L);
        paymentCreatedTodayBefore21Hours.setNewPaymentFields();

        LocalDateTime paymentCreationTimeToday = LocalDateTime.now().withHour(0);
        LocalDateTime todayTime21HoursAfterPaymentCreation = LocalDateTime.now().withHour(21);

        paymentCreatedTodayBefore21Hours.setCreateDateTime(paymentCreationTimeToday);

        PaymentTypeEnum mockedPaymentTypeEnum = mock(PaymentTypeEnum.class);
        paymentCreatedTodayBefore21Hours.setType(mockedPaymentTypeEnum);

        when(mockedPaymentTypeEnum.getCancellationCoefficient()).thenReturn(new BigDecimal("0.05"));

        when(paymentRepositoryMock.findById(paymentCreatedTodayBefore21Hours.getId()))
                .thenReturn(Optional.of(paymentCreatedTodayBefore21Hours));

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(todayTime21HoursAfterPaymentCreation);

            // when
            paymentService.cancelPayment(paymentCreatedTodayBefore21Hours.getId());

            // then
            assertThat(paymentCreatedTodayBefore21Hours.getCancellationFee()).isEqualTo(new BigDecimal("1.05"));
        }
    }
}