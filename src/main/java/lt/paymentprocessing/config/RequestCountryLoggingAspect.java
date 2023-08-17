package lt.paymentprocessing.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lt.paymentprocessing.client.CountryClient;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Slf4j
public class RequestCountryLoggingAspect {

    private static final List<String> HEADER_CANDIDATES_FOR_IP = List.of(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    );

    private final  HttpServletRequest httpServletRequest;

    private final CountryClient countryClient;

    public RequestCountryLoggingAspect(HttpServletRequest httpServletRequest, CountryClient countryClient) {
        this.httpServletRequest = httpServletRequest;
        this.countryClient = countryClient;
    }

    @Before("within(lt.paymentprocessing.controller.*)")
    public void logClientIpAndCountry() {
        String ip = getClientIp(httpServletRequest);
        String country = countryClient.resolveClientCountryByIp(ip);
        log.info("Client IP={} and country={}", ip, country);
    }

    private String getClientIp(HttpServletRequest request) {
        for (String header: HEADER_CANDIDATES_FOR_IP) {
            String ipList = request.getHeader(header);
            if (Strings.isNotBlank(ipList) && !"unknown".equalsIgnoreCase(ipList)) {
                return ipList.split(",")[0];
            }
        }
        return request.getRemoteAddr();
    }
}
