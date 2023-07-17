package lt.paymentprocessing.model;

import java.math.BigDecimal;

public enum PaymentTypeEnum {
    TYPE1(new BigDecimal("0.05")),
    TYPE2(new BigDecimal("0.1")),
    TYPE3(new BigDecimal("0.15"));

    private final BigDecimal cancellationCoefficient;

    PaymentTypeEnum(BigDecimal cancellationCoefficient) {
        this.cancellationCoefficient = cancellationCoefficient;
    }

    public BigDecimal getCancellationCoefficient() {
        return cancellationCoefficient;
    }
}
