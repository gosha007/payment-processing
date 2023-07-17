package lt.paymentprocessing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * JSON response example
 *
 * {
 * 	"id":"1",
 * 	"message":"Payment successfully created"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponseDto {
    private Long id;

    @JsonProperty("id_list")
    private List<Long> idList;

    private String message;

    @JsonProperty("cancellation_fee")
    private BigDecimal cancellationFee;
}
