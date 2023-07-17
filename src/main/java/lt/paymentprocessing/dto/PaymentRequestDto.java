package lt.paymentprocessing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@JsonTypeInfo(
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        use = JsonTypeInfo.Id.NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentType1RequestDto.class, name = "TYPE1"),
        @JsonSubTypes.Type(value = PaymentType2RequestDto.class, name = "TYPE2"),
        @JsonSubTypes.Type(value = PaymentType3RequestDto.class, name = "TYPE3")
})
@Data
@NoArgsConstructor
public abstract class PaymentRequestDto {

    @NotNull
    @Pattern(regexp = "TYPE1|TYPE2|TYPE3")
    protected String type;

    @NotNull
    @Positive
    protected BigDecimal amount;

    @NotBlank
    @JsonProperty("deptor_iban")
    protected String deptorIban;

    @NotBlank
    @JsonProperty("creditor_iban")
    protected String creditorIban;
}
