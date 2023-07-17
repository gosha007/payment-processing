package lt.paymentprocessing.service;

import lombok.extern.slf4j.Slf4j;
import lt.paymentprocessing.dto.CountryResponseDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Service
public class CountryServiceImpl implements CountryService {

    private static final String COUNTRY_RESOLVER_API_ROOT_URI = "http://ip-api.com";

    private final RestTemplate restTemplate;

    public CountryServiceImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .rootUri(COUNTRY_RESOLVER_API_ROOT_URI)
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(3000))
                .build();
    }
    @Override
    @Cacheable(value="countries", key = "#ip")
    public String resolveClientCountryByIP(String ip) {
        CountryResponseDto countryResponseDto = null;
        try {
            countryResponseDto =
                    restTemplate.getForObject("/json/{ip}", CountryResponseDto.class, ip);
        } catch (Exception ex) {
            log.warn("Could not resolve client country by IP={}. Exception = {}", ip, ex.getMessage());
        }
        return (countryResponseDto != null) ? countryResponseDto.getCountry() : "";
    }
}
