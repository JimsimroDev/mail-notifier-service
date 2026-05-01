package uk.jimsimrodev.notifier.enums;

import org.springframework.http.HttpStatus;

public enum ApiError {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Error de validación en los datos enviados"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno en el servidor"),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Erros no se encuetra ese servicio");

    private final HttpStatus httpStatus;
    private final String message;

    ApiError(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus geHttpStatus() {
        return this.httpStatus;
    }

    public String getMessage() {
        return this.message;
    }

}
