package uk.jimsimrodev.notifier.exception;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import uk.jimsimrodev.notifier.dto.ErrorDTO;
import uk.jimsimrodev.notifier.enums.ApiError;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);
    @ExceptionHandler(ApiMailException.class)
    public ResponseEntity<ErrorDTO> duplicateResource(ApiMailException e, WebRequest request) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorDTO(e.getDescription(), e.getReasons()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> reasons = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            reasons.add(String.format("%s - %s", error.getField(), error.getDefaultMessage()));
        }

        LOGGER.error("Reason del error {}",reasons.toString());
        return ResponseEntity.status(ApiError.BAD_REQUESTS.geHttpStatus())
                .body(new ErrorDTO(ApiError.BAD_REQUESTS.getMessage(), reasons));
    }
}
