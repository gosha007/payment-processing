package lt.paymentprocessing.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ResponseBodyWithErrors responseBodyWithErrors = new ResponseBodyWithErrors(
                HttpStatus.valueOf(statusCode.value()),
                errors,
                request.getDescription(false));

        return new ResponseEntity<>(
                responseBodyWithErrors.getResponseBody(),
                responseBodyWithErrors.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {
        log.warn("Returning HTTP 400 Bad Request | " + e.getMessage());
        List<String> errors = List.of(e.getMessage());

        ResponseBodyWithErrors responseBodyWithErrors = new ResponseBodyWithErrors(
                HttpStatus.BAD_REQUEST,
                errors,
                request.getDescription(false));

        return new ResponseEntity<>(
                responseBodyWithErrors.getResponseBody(),
                responseBodyWithErrors.getHttpStatus());
    }

    @ExceptionHandler(PaymentCancellationException.class)
    public ResponseEntity<Object> handlePaymentCancellationError(PaymentCancellationException ex) {
        List<String> errors = List.of(ex.getMessage());

        ResponseBodyWithErrors responseBodyWithErrors = new ResponseBodyWithErrors(
                HttpStatus.BAD_REQUEST, errors);

        return new ResponseEntity<>(
                responseBodyWithErrors.getResponseBody(),
                responseBodyWithErrors.getHttpStatus());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Object> handlePaymentNotFound(PaymentNotFoundException ex) {
        List<String> errors = List.of(ex.getMessage());

        ResponseBodyWithErrors responseBodyWithErrors = new ResponseBodyWithErrors(
                HttpStatus.NOT_FOUND, errors);

        return new ResponseEntity<>(
                responseBodyWithErrors.getResponseBody(),
                responseBodyWithErrors.getHttpStatus());
    }
}