package lt.paymentprocessing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * JSON request example
 *
 * {
 * 	"type":"TYPE2",
 * 	"amount":"2000",
 * 	"currency":"USD",
 * 	"deptor_iban":"LT121000011101001000",
 *  "creditor_iban":"EE382200221020145685",
 *  "details":"any free text details"
 * }
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentType2RequestDto extends PaymentRequestDto {

    @NotNull
    @Pattern(regexp = "USD")
    private String currency;

    private String details;
}
