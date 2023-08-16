package lt.paymentprocessing.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import lt.paymentprocessing.dto.CountryResponseDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public class CountryClient {

    private static final String COUNTRY_RESOLVER_API_BASE_URL = "http://ip-api.com";

    private final WebClient webClient;

    public CountryClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(3)));

        this.webClient = builder
                .baseUrl(COUNTRY_RESOLVER_API_BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Cacheable(value="countries", key = "#ip")
    public String resolveClientCountryByIp(String ip) {
        CountryResponseDto countryResponseDto = null;
        try {
            countryResponseDto = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/json/{ip}")
                            .build(ip))
                    .retrieve()
                    .bodyToMono(CountryResponseDto.class)
                    .block();
        } catch (WebClientException ex) {
            log.warn("Could not resolve client country by IP={}. Exception = {}", ip, ex.getMessage());
        }
        return (countryResponseDto != null) ? countryResponseDto.getCountry() : "";
    }
}
