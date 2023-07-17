package lt.paymentprocessing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
 * 	"type":"TYPE3",
 * 	"amount":"300",
 * 	"currency":"EUR",
 * 	"deptor_iban":"LT121000011101001000",
 *  "creditor_iban":"EE382200221020145685",
 *  "creditor_bank_bic":"EVIULT2VXXX"
 * }
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentType3RequestDto extends PaymentRequestDto {

    @NotNull
    @Pattern(regexp = "EUR|USD")
    private String currency;

    @NotBlank
    @JsonProperty("creditor_bank_bic")
    private String creditorBankBic;
}
