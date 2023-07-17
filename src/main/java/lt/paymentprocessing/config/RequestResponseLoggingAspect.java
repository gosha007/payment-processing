package lt.paymentprocessing.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lt.paymentprocessing.service.CountryService;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Slf4j
public class RequestResponseLoggingAspect {

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

    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CountryService countryService;

    @Before("within(lt.paymentprocessing.controller.*)")
    public void endpointBefore(JoinPoint p) {
        logRequestURIAndClientInfo();

        log.info(p.getTarget().getClass().getSimpleName() + " " + p.getSignature().getName() + " START");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Object[] signatureArgs = p.getArgs();
        try {
            if (signatureArgs[0] != null) {
                log.info("\nRequest object: \n" + objectMapper.writeValueAsString(signatureArgs[0]));
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON request: " + e.getMessage());
        }
    }

    @AfterReturning(value = ("within(lt.paymentprocessing.controller.*)"), returning = "returnValue")
    public void endpointAfterReturning(JoinPoint p, Object returnValue) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            log.info("\nResponse object: \n" + objectMapper.writeValueAsString(returnValue));
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON response: " + e.getMessage());
        }
        log.info(p.getTarget().getClass().getSimpleName() + " " + p.getSignature().getName() + " END");
    }

    public void logRequestURIAndClientInfo() {
        // request URI with params
        String queryString = httpServletRequest.getQueryString();
        log.info("Request URI: " + httpServletRequest.getRequestURI()
                + (Strings.isNotBlank(queryString) ? "?" + queryString : "")
        );

        // client IP and country
        String ip = getClientIP(httpServletRequest);
        String country = countryService.resolveClientCountryByIP(ip);
        log.info("Client IP={} and country={}", ip, country);
    }

    private String getClientIP(HttpServletRequest request) {
        for (String header: HEADER_CANDIDATES_FOR_IP) {
            String ipList = request.getHeader(header);
            if (Strings.isNotBlank(ipList) && !"unknown".equalsIgnoreCase(ipList)) {
                return ipList.split(",")[0];
            }
        }
        return request.getRemoteAddr();
    }
}
