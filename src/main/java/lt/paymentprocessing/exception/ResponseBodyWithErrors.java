package lt.paymentprocessing.exception;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseBodyWithErrors {

    private HttpStatus httpStatus;
    private List<String> errors;
    private String path;

    public ResponseBodyWithErrors(HttpStatus httpStatus, List<String> errors) {
        this.httpStatus = httpStatus;
        this.errors = errors;
    }

    public ResponseBodyWithErrors(HttpStatus httpStatus, List<String> errors, String path) {
        this(httpStatus, errors);
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    public Map<String, Object> getResponseBody() {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", httpStatus.value());
        responseBody.put("errors", errors);

        if (Strings.isNotBlank(path)) {
            responseBody.put("path", path);
        }

        return responseBody;
    }
}
