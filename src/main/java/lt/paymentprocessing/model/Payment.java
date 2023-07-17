package lt.paymentprocessing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Payment {

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentTypeEnum type;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    @NotBlank
    @Column(name = "deptor_iban")
    private String deptorIban;

    @NotBlank
    @Column(name = "creditor_iban")
    private String creditorIban;

    @Column(name = "creditor_bank_bic")
    private String creditorBankBic;

    private String details;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

    @Column(name = "cancellation_fee")
    private BigDecimal cancellationFee;

    @NotNull
    @Column(name = "create_date_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime createDateTime;

    public void setNewPaymentFields() {
        this.status = PaymentStatusEnum.ACTIVE;
        this.createDateTime = LocalDateTime.now();
    }

    public void setCancelledPaymentFields(BigDecimal cancellationFee) {
        this.status = PaymentStatusEnum.CANCELLED;
        this.cancellationFee = cancellationFee;
    }
}
