package uk.jimsimrodev.notifier.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import uk.jimsimrodev.notifier.enums.ApiError;

public class ApiMailException extends RuntimeException {

    private HttpStatus httpStatus;
    private String description;
    private List<String> reasons = new ArrayList<>();

    public ApiMailException(ApiError error) {
        this.httpStatus = error.geHttpStatus();
        this.description = error.getMessage();
    }

    public ApiMailException(HttpStatus httpStatus, String description, List<String> reasons) {
        this.httpStatus = httpStatus;
        this.description = description;
        this.reasons = reasons;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

}
