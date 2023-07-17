package lt.paymentprocessing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * JSON request example
 *
 * {
 * 	"type":"TYPE1",
 * 	"amount":"100",
 * 	"currency":"EUR",
 * 	"deptor_iban":"LT121000011101001000",
 *  "creditor_iban":"EE382200221020145685",
 *  "details":"any free text details"
 * }
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentType1RequestDto extends PaymentRequestDto {

    @NotNull
    @Pattern(regexp = "EUR")
    private String currency;

    @NotBlank
    private String details;
}
