package lt.paymentprocessing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CountryResponseDto {
    @JsonProperty("query")
    private String ip;

    private String country;
}
